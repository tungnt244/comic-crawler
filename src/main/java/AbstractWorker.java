import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWorker implements Worker, Runnable{

	TaskHolder taskHolder;

	String name;

	boolean stop;

	Thread thisThread;

	int sleepTimeOut = 20;

	int id;

	boolean cancellation;

	boolean paused;

	private static Map<String, Integer> nameToId = new HashMap<>();

	public AbstractWorker(String name, boolean cancellation) {
		this.name = name;
		this.id = generateId(name);
		this.cancellation = cancellation;
	}

	@Override
	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
		start();
	}

	private boolean start() {
		if((taskHolder != null) && beforeStart()) {
			thisThread = new Thread(this);
			thisThread.setName(String.format("%s.%d", name, id));
			thisThread.start();
			return true;
		}
		return false;
	}

	abstract boolean beforeStart();

	abstract void newTask();

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void run() {
		runUnsafe();
	}

	private void runUnsafe() {

	}

	private static synchronized int generateId(String name) {
		if(!nameToId.containsKey(name)) {
			nameToId.put(name, 1);
			return 0;
		} else {
			int result = nameToId.get(name);
			nameToId.put(name, result+1);
			return result;
		}
	}
}
