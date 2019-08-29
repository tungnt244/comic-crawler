package crawler;

public interface Worker {

	void setTaskHolder(TaskHolder taskHolder);

	String getName();

	void setSleepTimeout(int timeout);

	boolean start();

	Thread stop();

	void fillStatistic(CrawlStatistic statistic);

	void pause();

	boolean isStopped();

	void newTask(Task task);

	void everyCycle();

	void releaseTask(Task task);
}
