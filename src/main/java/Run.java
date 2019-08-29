import crawler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Run {

	private static Logger LOG = LoggerFactory.getLogger(Run.class);

	public static void main(String[] args) throws InterruptedException, IOException {

		//generate data
		UrlRequestPacket urlRequestPacket = new UrlRequestPacket();
		List<UrlRequestPacket> data = urlRequestPacket.generatePack();
		LinkedList<Task> finalTasks = new LinkedList<>();
		for (UrlRequestPacket pack :
				data) {
			List<Task> tasks = pack.getUrlList()
					.stream()
					.map(url -> new CrawlTask(pack.getQueueName(), url, pack.getCrawlDelay()))
					.collect(Collectors.toList());
			finalTasks.addAll(tasks);
		}

		List<AbstractWorker> workers = new LinkedList<>();
		workers.add(new DelayWorker());
		workers.add(new DummyWorker());
		workers.add(new FinishWorker("finisher", false));
		List<String> chain = workers.stream().map(AbstractWorker::getName).collect(Collectors.toList());
		chain.add("taskProvider");
		TaskHolder taskHolder = new SimpleTaskHolder();
		taskHolder.setWorkerChain(chain);

		workers.forEach(abstractWorker -> {
			abstractWorker.setTaskHolder(taskHolder);
		});
		TaskProvider taskProvider = new TaskProvider();
		taskProvider.setListTasks(finalTasks);
		taskProvider.setTaskHolder(taskHolder);
		System.out.println("hello there");
		workers.add(taskProvider);

		workers.get(2).start();

	}
}
