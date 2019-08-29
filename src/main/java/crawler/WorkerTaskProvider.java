package crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class WorkerTaskProvider extends AbstractWorker {

	private static Logger LOG = LoggerFactory.getLogger(WorkerTaskProvider.class);

	private static final String PROP_NAME_TASK_PROCESS_START_TIME_MILLIS = "PROP_NAME_TASK_PROCESS_START_TIME_MILLIS";
	private static final String PROP_NAME_DESCRIPTOR_ID = "descriptorId";

	long normalTaskTime;
	LinkedList<Task> listTasks;
	private long createdTasks;

	public WorkerTaskProvider() {
		this("taskProvider", false);
	}

	public WorkerTaskProvider(String name, boolean cancellation) {
		super(name, cancellation);
	}

	@Override
	boolean beforeStart() {
		return true;
	}

	@Override
	public void newTask(Task tsk) {
		CrawlTask task = (CrawlTask) tsk;
		handleTask(task);
//		normalTaskTime += (System.currentTimeMillis() - (long) task.getAttribute(PROP_NAME_TASK_PROCESS_START_TIME_MILLIS));
		tasksInProgress.decrementAndGet();
	}

	@Override
	public void everyCycle() {
		addTaskFromDescriptors();
	}

	private boolean flag = false;

	// here is the key to add more tasks
	private void addTaskFromDescriptors() {
		System.out.println("list tasks: "+ listTasks.size());

		while(!listTasks.isEmpty()) {
			Task task = listTasks.poll();
			taskHolder.addTask(task);
			++createdTasks;
		}
		if(!flag) {
			addTaskFromSomeWhere();
			flag = !flag;
		}
	}

	public void addTaskFromSomeWhere() {
		Runnable temp = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				UrlRequestPacket urlRequestPacket = new UrlRequestPacket();
				List<UrlRequestPacket> data = null;
				try {
					data = urlRequestPacket.generateLatePack();
				} catch (IOException e) {
					e.printStackTrace();
				}
				LinkedList<Task> finalTasks = new LinkedList<>();
				for (UrlRequestPacket pack :
						data) {
					List<Task> tasks = pack.getUrlList()
							.stream()
							.map(url -> new CrawlTask(pack.getQueueName(), url, pack.getCrawlDelay()))
							.collect(Collectors.toList());
					finalTasks.addAll(tasks);
				}
				setListTasks(finalTasks);
			}
		};
		Thread tempT = new Thread(temp);
		tempT.start();

	}

	@Override
	public void setSleepTimeout(int timeout) {

	}

	@Override
	public boolean isStopped() {
		return false;
	}

	public void handleTask(Task task) {
		System.out.println("Handle thing for 3s");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		task.putAttribute(CrawlTask.ATTR_FIRST_CRAWLING_TIME, System.currentTimeMillis());
		task.putAttribute(CrawlTask.ATTR_LAST_CRAWLING_TIME, System.currentTimeMillis());
	}

	public void setListTasks(LinkedList<Task> finalTasks) {
		if(listTasks == null) {
			listTasks = new LinkedList<>();
		}
		listTasks.addAll(finalTasks);
	}
}
