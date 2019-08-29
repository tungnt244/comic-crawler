package crawler;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.util.LinkedList;

public class CrawlTask extends AbstractTask implements Serializable {

	private static final long serialVersionUID = -924239337665465431L;

	/**
	 * type: new LinkedList<{@link InetAddress}>
	 * default value: null
	 */
	static final String ATTR_IP = "ATTR_IP";

	static final String ATTR_URL_FLAGS = "ATTR_URL_FLAGS";

	static final String ATTR_CRAWL_DELAY = "ATTR_CRAWL_DELAY";

	static final String ATTR_LAST_CRAWLING_TIME = "ATTR_LAST_CRAWLING_TIME";

	static final String ATTR_FIRST_CRAWLING_TIME = "ATTR_FIRST_CRAWLING_TIME";

	private String queueName;

	private URL url;

	private int crawlDelay;

	private String host;


	private final String originalHost;
	private final CrawlTask previousTaskInChain;

	public CrawlTask(String queueName, URL url) {
		this(queueName, url, 0, null);
	}

	public CrawlTask(String queueName, URL url, int crawlDelay) {
		this(queueName, url, crawlDelay, null);
	}

	public CrawlTask(String queueName, URL url, int crawlDelay, CrawlTask previousTaskInChain) {
		this.queueName = queueName;
		this.url = url;
		this.originalHost = url.getHost();
		this.previousTaskInChain = previousTaskInChain;
		this.crawlDelay = crawlDelay;
	}


	public URL getUrl() {
		return url;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public LinkedList<InetAddress> getIPs() {
		Object object = attributes.get(CrawlTask.ATTR_IP);
		return object == null ? null : (LinkedList<InetAddress>) object;
	}

	public boolean isBannedByRobottxt() {
		return mockIsSetFlag(getFlags());
	}

	public int getCrawlDelay() {
		return crawlDelay;
	}

	public long getFlags() {
		return (long) attributes.getOrDefault(ATTR_URL_FLAGS, 0L);
	}

	public boolean mockIsSetFlag(long flags) {
		return true;
	}

	public long getLastCrawlingTime() {
		return (long) attributes.getOrDefault(ATTR_LAST_CRAWLING_TIME, -1L);
	}

	public String getQueueName() {
		return queueName;
	}

	@Override
	public String toString() {
		return "CrawlTask{" +
				"queueName='" + queueName + '\'' +
				", url=" + url +
				'}';
	}
}
