package crawler;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class TaskDescriptor {

	private int id;

	private boolean closed;

	private final LinkedList<URL> urlQueue = new LinkedList<>();

	private LinkedList<Task> tasks = new LinkedList<>();

	public boolean isClosed() {
		return closed;
	}

	public int getId() {
		return id;
	}

	public Task nextTask() {
		Task task = tasks.poll();
		task.putAttribute(CrawlTask.ATTR_FIRST_CRAWLING_TIME, System.currentTimeMillis());
		task.putAttribute(CrawlTask.ATTR_LAST_CRAWLING_TIME, System.currentTimeMillis());
		return task;
	}

	public void setTasks(LinkedList<Task> tasks){
		this.tasks = tasks;
	}
}
