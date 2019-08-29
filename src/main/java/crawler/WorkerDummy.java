package crawler;

public class WorkerDummy extends AbstractWorker {

	public WorkerDummy() {
		this("dummyWorker", false);
	}

	public WorkerDummy(String name, boolean cancellation) {
		super(name, cancellation);
	}

	@Override
	boolean beforeStart() {
		return true;
	}

	@Override
	public void newTask(Task task) {
		Util.pretendToWork(getThreadName(), 1000);

//		taskHolder.addTask(task);
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
