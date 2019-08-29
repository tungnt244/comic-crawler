package crawler;

public class FinishWorker extends AbstractWorker {

	public FinishWorker(String name, boolean cancellation) {
		super(name, cancellation);
	}

	@Override
	boolean beforeStart() {
		return true;
	}

	@Override
	public void setSleepTimeout(int timeout) {

	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public void newTask(Task task) {
		System.out.println(name + " new task");
		releaseTask(task);
	}
	static int count = 0;
	@Override
	public void everyCycle() {
		System.out.println(name + " every cycle");
		System.out.println(count++);
		pollTask();
	}
}
