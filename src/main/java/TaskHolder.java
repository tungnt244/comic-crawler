import java.util.List;

public interface TaskHolder {

	public Task poll(String workerName);

	public void releaseTask(Task task);

	public void setWorkerChain(List<String> chain);

	public void fillStatistic(CrawlStatistic crawlStatistic);

	public boolean addTask(Task task);

	public void removeTask(Task task);
}
