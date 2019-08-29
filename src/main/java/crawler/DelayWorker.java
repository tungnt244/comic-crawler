package crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DelayWorker extends AbstractWorker {

	private static final Logger LOG = LoggerFactory.getLogger(DelayWorker.class);

	private ConcurrentHashMap<String, HostHolder> queues = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, HostHolder> unlocked = new ConcurrentHashMap<>();
	private PriorityQueue<HostHolder> unlockedInOrder =
			new PriorityQueue<>(Comparator.comparing(HostHolder::getNextRequestTime));

	public DelayWorker() {
		this("delayWorker");
	}

	public DelayWorker(String name) {
		super(name, false);
	}

	@Override
	boolean beforeStart() {
		return true;
	}

	@Override
	public void newTask(Task tsk) {
		WorkerProfiler.polledTask(tsk, "delay boy");
		if (tsk instanceof CrawlTask) {
			CrawlTask task = (CrawlTask) tsk;

			List<InetAddress> taskAddresses = task.getIPs();


			// don't crawl without discovered ips
			if (taskAddresses == null || taskAddresses.isEmpty()) {
				releaseTask(task);
				return;
			}

			// don't crawl urls banned by robot.txt
			if (task.isBannedByRobottxt()) {
				releaseTask(task);
				return;
			}

			int delay = task.getCrawlDelay();

			if (delay < 0) {
				throw new RuntimeException("Crawl-delay isn't set for " + task.getUrl());
			}
			long lastCrawlingTime = task.getLastCrawlingTime();
			if (lastCrawlingTime < 0) {
				lastCrawlingTime = System.currentTimeMillis();
				LOG.debug("No lastCrawling time set for " + task.getUrl());
			}

			putInQueue(task, task.getQueueName(), task.getUrl().toString(), lastCrawlingTime, delay);

		} else {
			releaseTask(tsk);
		}
	}

	private synchronized void putInQueue(
			Task task, String queueName, String logMsg, long lastCrawlingTime,
			int delay) {
		boolean isUnlocked = false;
		HostHolder holder = queues.get(queueName);
		if (holder == null) {
			isUnlocked = true;
			holder = unlocked.get(queueName);
			if (holder == null) {
				if (lastCrawlingTime == -1) {
					holder = new HostHolder(queueName);
				} else {
					holder = new HostHolder(queueName, lastCrawlingTime);
					LOG.debug("Init {} with lastCrawlingtime {}", holder, lastCrawlingTime);
				}
				unlocked.put(queueName, holder);
				LOG.debug("Create new holder {} for task {} and put it in unlocked", holder, logMsg);
			} else {
				LOG.debug("Unlocked already has holder {} for task {}", holder, logMsg);
			}
		} else {
			LOG.debug("Task queue {} is already in queues holder {}", task, holder);
		}
		holder.setDelay(delay);
		holder.putTask(task);
		LOG.debug("Put task {} in holder {}", task, holder);
		if (isUnlocked) {
			//to reorder
			unlockedInOrder.remove(holder);
			unlockedInOrder.add(holder);
		}
	}

	@Override
	public void everyCycle() {
		synchronized (this) {
			if (unlocked.size() != unlockedInOrder.size()) {
				throw new RuntimeException("Unlocked collections have different size");
			}
			HostHolder hostHolder;
			while (!unlockedInOrder.isEmpty() && System.currentTimeMillis() >= unlockedInOrder.peek()
					.getNextRequestTime()) {
				hostHolder = unlockedInOrder.poll();
				unlocked.remove(hostHolder.getQueueName());
				Task task = hostHolder.getTask();
				if (task != null) {
					LOG.debug("Move from unlocked to queue holder {} release task {}", hostHolder, task);
					hostHolder.setTimeSinceTaskRelease(System.currentTimeMillis());
					queues.put(hostHolder.getQueueName(), hostHolder);
					releaseTask(task);
					WorkerProfiler.releasedTask(task, "delay boy");
				}
			}

		}
	}

	@Override
	public void setSleepTimeout(int timeout) {

	}

	@Override
	public void fillStatistic(CrawlStatistic statistic) {
		statistic.setProperty(String.format("%s.queue.size", getThreadName()), queues.size() + unlocked.size());
		super.fillStatistic(statistic);
	}

	@Override
	public void pause() {

	}

	@Override
	public boolean isStopped() {
		return false;
	}

	private class HostHolder {
		private Queue<Task> tasks = new LinkedList<>();
		private int delay;
		private long timeSinceLastRequest;
		private long timeSinceTaskRelease = System.currentTimeMillis();
		private String queueName;

		HostHolder(String queueName) {
			this.timeSinceLastRequest = System.currentTimeMillis();
			this.queueName = queueName;
		}

		HostHolder(String queueName, long lastCrawlingTime) {
			this.timeSinceLastRequest = lastCrawlingTime;
			this.queueName = queueName;
		}

		long getNextRequestTime() {
			return timeSinceLastRequest + delay;
		}

		void putTask(Task task) {
			tasks.offer(task);
		}

		private String getLogMessage(Task task) {
			if (task instanceof CrawlTask) {
				return ((CrawlTask) task).getUrl().toString();
			}
			return null;
		}

		public Task getTask() {
			return tasks.poll();
		}

		public String getQueueName() {
			return queueName;
		}

		public int getDelay() {
			return delay;
		}

		public void setDelay(int delay) {
			this.delay = delay;
		}

		public long getTimeSinceLastRequest() {
			return this.timeSinceLastRequest;
		}

		public long getTimeSinceTaskRelease() {
			return this.timeSinceTaskRelease;
		}

		public void setTimeSinceLastRequest(long timeSinceLastRequest) {
			this.timeSinceLastRequest = timeSinceLastRequest;
		}

		public void setTimeSinceTaskRelease(long timeSinceTaskRelease) {
			this.timeSinceTaskRelease = timeSinceTaskRelease;
		}

		public boolean isEmpty() {
			return tasks.isEmpty();
		}

		public String toString() {
			return "Hostholder queue: " + queueName + " tasks: " + tasks.stream()
					.map(this::getLogMessage)
					.collect(Collectors.joining(",")) + " timeSinceLastRequest: " + timeSinceLastRequest;
		}
	}
}
