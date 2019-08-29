package crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;


public class TaskProvider extends AbstractWorker {

	private static Logger LOG = LoggerFactory.getLogger(TaskProvider.class);

	private static final String PROP_NAME_TASK_PROCESS_START_TIME_MILLIS = "PROP_NAME_TASK_PROCESS_START_TIME_MILLIS";
	private static final String PROP_NAME_DESCRIPTOR_ID = "descriptorId";

	long normalTaskTime;
	LinkedList<Task> listTasks;
	private long createdTasks;

	public TaskProvider() {
		this("taskProvider", false);
	}

	public TaskProvider(String name, boolean cancellation) {
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
		normalTaskTime += (System.currentTimeMillis() - (long) task.getAttribute(PROP_NAME_TASK_PROCESS_START_TIME_MILLIS));
		tasksInProgress.decrementAndGet();
	}

	@Override
	public void everyCycle() {
		addTaskFromDescriptors();
	}

	private void addTaskFromDescriptors() {
		System.out.println("list tasks remain"+ listTasks.size());
		Task task = listTasks.poll();
		task.putAttribute(PROP_NAME_TASK_PROCESS_START_TIME_MILLIS, System.currentTimeMillis());
		taskHolder.addTask(task);
		++createdTasks;
	}

	@Override
	public void setSleepTimeout(int timeout) {

	}

	@Override
	public boolean isStopped() {
		return false;
	}

	public void handleTask(Task task) {
		System.out.println("Doing thing for 3s");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		task.putAttribute(CrawlTask.ATTR_FIRST_CRAWLING_TIME, System.currentTimeMillis());
		task.putAttribute(CrawlTask.ATTR_LAST_CRAWLING_TIME, System.currentTimeMillis());
	}

	public void setListTasks(LinkedList<Task> finalTasks) {
		listTasks = finalTasks;
	}
}
