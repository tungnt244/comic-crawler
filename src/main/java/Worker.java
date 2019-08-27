public interface Worker {

	public void setTaskHolder(TaskHolder taskHolder);

	public String getName();

	public void setSleepTimeout(int timeout);

	public Thread stop();

	public void fillStatistic(CrawlStatistic statistic);

	public void pause();

	public boolean isStopped();
}
