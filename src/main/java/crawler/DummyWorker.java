package crawler;

public class DummyWorker extends AbstractWorker {

	public DummyWorker() {
		this("dummy boy", false);
	}

	public DummyWorker(String name, boolean cancellation) {
		super(name, cancellation);
	}

	@Override
	boolean beforeStart() {
		return true;
	}

	@Override
	public void newTask(Task task) {
		System.out.println("new task dummy");
		taskHolder.addTask(task);
		releaseTask(task);
	}

	@Override
	public void everyCycle() {
		System.out.println("every cycle " + name);
		pollTask();
	}

	@Override
	public void setSleepTimeout(int timeout) {

	}

	@Override
	public boolean isStopped() {
		return false;
	}
}
