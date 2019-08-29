package crawler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractTask implements Task, Serializable {


	private static final long serialVersionUID = 5102979662628316676L;

	private static long ID = 0;

	protected long id = 0;

	protected final Map<String, Serializable> attributes = new HashMap<>();

	protected String lastWorker;

	StringBuilder error = new StringBuilder();

	private volatile boolean isCancelled = false;

	public AbstractTask() {
		this.id = generateId();
	}

	@Override
	public void putAttribute(String key, Serializable value) {
		synchronized (attributes) {
			attributes.put(key, value);
		}
	}

	@Override
	public Serializable getAttribute(String key) {
		synchronized (attributes) {
			return attributes.get(key);
		}
	}

	@Override
	public String getLastWorker() {
		return lastWorker;
	}

	@Override
	public void setLastWorker(String lastWorker) {
		this.lastWorker = lastWorker;
	}

	@Override
	public Set<String> getAttributeNames() {
		synchronized (attributes) {
			return attributes.keySet();
		}
	}

	public void clearAttributes(String key) {
		synchronized (attributes) {
			attributes.remove(key);
		}
	}

	@Override
	public String getError() {
		if (error.length() > 0) {
			return error.toString();
		} else {
			return null;
		}
	}

	@Override
	public void appendError(String newError) {
		error.append(newError);
		error.append("\n");
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void cancel() {
		this.isCancelled = true;
	}

	@Override
	public String printAttributes() {
		synchronized (attributes) {
			return attributes.toString();
		}
	}

	private static synchronized long generateId() {
		ID++;
		return ID;
	}

	@Override
	public int compareTo(Task o) {
		if (o instanceof AbstractTask) {
			AbstractTask task = (AbstractTask) o;
			if (this.id < task.id) {
				return -1;
			} else if (this.id > task.id) {
				return 1;
			} else return 0;
		} else return -1;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}
}
