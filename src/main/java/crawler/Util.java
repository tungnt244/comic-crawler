package crawler;

public class Util {
	public static void pretendToWork(String name, int sleepTime) {
		System.out.println("Sleep for " + sleepTime+": "+name);
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void logPollTask(String name) {
		System.out.println("poll task: " + name);
	}
}
