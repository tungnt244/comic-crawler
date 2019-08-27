import java.io.Serializable;
import java.util.Set;

public interface Task extends Comparable<Task>{

	public void putAttribute(String key, Serializable value);

	public Serializable getAttribute(String key);

	public String getLastWorker();

	public Set<String> getAttributeNames();

	public String getError();

	public void appendError(String error);

	public boolean isCancelled();

	public void cancel();

	String printAttributes();
}
