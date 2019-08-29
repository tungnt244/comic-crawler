package crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractWorker implements Worker, Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractWorker.class);

	TaskHolder taskHolder;

	String name;

	boolean stop;

	Thread thisThread;

	int sleepTimeOut = 2;

	int id;

	boolean cancellation;

	boolean paused;

	protected AtomicInteger tasksInProgress = new AtomicInteger();

	protected AtomicInteger tasksReleased = new AtomicInteger();

	protected AtomicInteger tasksPolled = new AtomicInteger();

	private static Map<String, Integer> nameToId = new HashMap<>();

	private long sleptMilis;

	private long startTime;

	public AbstractWorker(String name, boolean cancellation) {
		this.name = name;
		this.id = generateId(name);
		this.cancellation = cancellation;
	}

	@Override
	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		start();
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getThreadName() {
		return thisThread.getName();
	}

	@Override
	public void run() {
		runUnsafe();
	}

	private void runUnsafe() {
		startTime = System.currentTimeMillis();
		while(!stop) {
			everyCycle();
			while(paused) {
				try {
					Thread.sleep(20*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			if(!pollTask()) {
				try {
					System.out.println("No task left: "+getThreadName());
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sleptMilis += sleepTimeOut;
			}
//			if(taskHolder.getTasksCount() <= 0) {
//				stop = true;
//			}
		}
//		System.out.println("Runnnnnnn");
	}

	boolean pollTask() {
		Util.logPollTask(getThreadName());
		Task task = null;
		try {
			do {
				if(this.getClass() != WorkerTaskProvider.class) {
					task = taskHolder.poll(name);
				} else {
					task =taskHolder.removeTask(name);
				}

				if(task == null) return false;
			} while (cancellation && task.isCancelled());
			System.out.println("in processed " +tasksInProgress.incrementAndGet());
			System.out.println("polled " +tasksPolled.incrementAndGet());
			newTask(task);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public void fillStatistic(CrawlStatistic statistic) {
		statistic.setProperty(String.format("%s.taskInProgress", getThreadName()), tasksInProgress.get());
		statistic.setProperty(String.format("%s.tasksReleased", getThreadName()), tasksReleased.get());
		statistic.setProperty(String.format("%s.tasksPolled", getThreadName()), tasksPolled.get());

		long sleptPerc = sleptMilis * 100 / (System.currentTimeMillis() - startTime + 1);
		statistic.setProperty(String.format("%s.slept.pct", getThreadName()), sleptPerc);
	}

	public boolean start() {
		if ((taskHolder != null) && beforeStart()) {
			thisThread = new Thread(this);
			thisThread.setName(String.format("%s.%d", name, id));
			thisThread.start();
			System.out.println("started: " + getThreadName());
			return true;
		}
		return false;
	}

	@Override
	public Thread stop() {
		this.stop = true;
		return thisThread;
	}

	private static synchronized int generateId(String name) {
		if (!nameToId.containsKey(name)) {
			nameToId.put(name, 1);
			return 0;
		} else {
			int result = nameToId.get(name);
			nameToId.put(name, result + 1);
			return result;
		}
	}

	public void pause() {
		paused = !paused;
	}

	abstract boolean beforeStart();

	abstract public void newTask(Task task);

	abstract public void everyCycle();

	public void releaseTask(Task task) {
		tasksInProgress.decrementAndGet();
		tasksReleased.incrementAndGet();
		task.setLastWorker(name);
		System.out.printf("release task %s %s\n", task, getThreadName());
		taskHolder.releaseTask(task);
	}
}
