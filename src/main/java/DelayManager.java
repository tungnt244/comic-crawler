public class DelayManager extends AbstractWorker {

	public DelayManager(String name) {
		super(name, true);
	}

	@Override
	boolean beforeStart() {
		return true;
	}

	@Override
	void newTask() {

	}

	@Override
	public void setSleepTimeout(int timeout) {

	}

	@Override
	public Thread stop() {
		return null;
	}

	@Override
	public void fillStatistic(CrawlStatistic statistic) {

	}

	@Override
	public void pause() {

	}

	@Override
	public boolean isStopped() {
		return false;
	}
}
