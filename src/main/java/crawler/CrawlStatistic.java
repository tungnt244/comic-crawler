package crawler;

import it.unimi.dsi.fastutil.objects.Object2LongAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;

public class CrawlStatistic {

	private Object2LongMap<String> props = new Object2LongAVLTreeMap<>();

	public synchronized void setProperty(String propName, long num) {
		String name = propName.replace("\n", "");
		props.put(name, num);
	}

	public synchronized void dumpStatistic(CrawlStatistic stat) {
		for (String key : props.keySet()) {
			stat.setProperty(key, props.getLong(key));
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("CrawlStatistic{\n");
		sb.append("props={\n");
		for (String key : props.keySet()) {
			sb.append(key);
			sb.append("=");
			sb.append(props.getLong(key));
			sb.append("\n");
		}
		sb.append('}');
		DelayWorker temp = new DelayWorker();
		temp.getThreadName();
		return sb.toString();
	}
}
