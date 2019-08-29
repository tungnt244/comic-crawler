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

		WorkerTaskProvider workerTaskProvider = new WorkerTaskProvider();
		List<AbstractWorker> workers = new LinkedList<>();
		workers.add(new WorkerDelay());
		workers.add(new WorkerDummy());
		workers.add(new WorkerFinish());
		List<String> chain = workers.stream().map(AbstractWorker::getName).collect(Collectors.toList());
		chain.add(workerTaskProvider.getName());
		TaskHolder taskHolder = new SimpleTaskHolder();
		taskHolder.setWorkerChain(chain);


		workerTaskProvider.setListTasks(finalTasks);
		workerTaskProvider.setTaskHolder(taskHolder);

		workers.forEach(abstractWorker -> {
			abstractWorker.setTaskHolder(taskHolder);
		});
		System.out.println("hello there");
		workers.add(workerTaskProvider);
		workers.get(0).start();

	}
}
