package crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerProfiler {

	private static final Logger LOG = LoggerFactory.getLogger(WorkerProfiler.class);

	public static void polledTask(Task task, String worker) {
		LOG.debug("{} polled {} t: {}", ((AbstractTask) task).id, worker, System.currentTimeMillis());
	}

	public static void releasedTask(Task task, String worker) {
		LOG.debug("{} released {} t: {}", ((AbstractWorker) task).id, worker, System.currentTimeMillis());
	}
}
