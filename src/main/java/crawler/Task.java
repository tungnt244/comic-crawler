package crawler;

import java.io.Serializable;
import java.net.URL;
import java.util.Set;

public interface Task extends Comparable<Task>{

	void putAttribute(String key, Serializable value);

	Serializable getAttribute(String key);

	String getLastWorker();

	public void setLastWorker(String lastWorker);

	public Set<String> getAttributeNames();

	public String getError();

	public void appendError(String error);

	public boolean isCancelled();

	public void cancel();

	String printAttributes();

	void setUrl(URL url);
}
