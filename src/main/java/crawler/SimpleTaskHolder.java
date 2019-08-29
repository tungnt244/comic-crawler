package crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SimpleTaskHolder implements TaskHolder {

	private static Logger LOG = LoggerFactory.getLogger(SimpleTaskHolder.class);

	List<String> workers = new ArrayList<>();

	private Map<String, BlockingQueue<Task>> workerTaskQueueMap = new HashMap<>();

	private final static int QUEUE_CAPACITY = 1500;

	int tasksCount = 0;

	@Override
	public Task poll(String workerName) {
		Task result = null;

		BlockingQueue<Task> queue = workerTaskQueueMap.get(workerName);
		try {
			result = queue.poll(0, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			LOG.error("Take operation was interrupted");
		}
		return result;
	}

	@Override
	public void releaseTask(Task task) {
		BlockingQueue<Task> queue = workerTaskQueueMap.get(findNextWorker(task));
		try {
			queue.put(task);
		} catch (InterruptedException e) {
			System.out.println("Something happen");
		}
	}

	@Override
	public void setWorkerChain(List<String> chain) {
		workerTaskQueueMap = new HashMap<>();
		for(String worker: chain) {
			workerTaskQueueMap.put(worker, new ArrayBlockingQueue<Task>(QUEUE_CAPACITY));
		}
		workers = new LinkedList<>(chain);
	}

	@Override
	public void fillStatistic(CrawlStatistic crawlStatistic) {

	}

	@Override
	public boolean addTask(Task task) {
		boolean result = false;

		BlockingQueue<Task> queue = workerTaskQueueMap.get(findNextWorker(task));
		try {
			queue.put(task);
			result =true;
			LOG.debug("new {}", task);
		} catch (InterruptedException e) {
			LOG.error("put was interrupted {}", task);
		}
		tasksCount++;
		return result;
	}

	@Override
	public synchronized Task removeTask(String workerName) {
		Task result = poll(workerName);

		if(result!=null) {
			LOG.debug("{} removed task {}", workerName, result.toString());
		}
		tasksCount--;
		return result;
	}

	private String findNextWorker(Task task) {
		String result;
		// when will this one get null
		if(task.getLastWorker() == null) {
			result = workers.get(0);
		} else {
			result = workers.get(workers.indexOf(task.getLastWorker()) + 1);
		}
		return result;
	}

	public int getTasksCount() {
		return tasksCount;
	}
}
