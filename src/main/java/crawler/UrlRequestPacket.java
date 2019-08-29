package crawler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public class UrlRequestPacket {

	public UrlRequestPacket() {
	}

	public UrlRequestPacket(String queueName, String host, List<URL> urlList, int crawlDelay) {
		this.queueName = queueName;
		this.host = host;
		this.urlList = urlList;
		this.crawlDelay = crawlDelay;
	}

	String queueName;

	String host;

	List<URL> urlList;

	int crawlDelay;

	public String getQueueName() {
		return queueName;
	}

	public String getHost() {
		return host;
	}

	public List<URL> getUrlList() {
		return urlList;
	}

	public int getCrawlDelay() {
		return crawlDelay;
	}

	public List<UrlRequestPacket> generatePack() throws IOException {
		String file1 = this.getClass().getClassLoader().getResource("source1").getPath();
		String file2 = this.getClass().getClassLoader().getResource("source2").getPath();
		String file3 = this.getClass().getClassLoader().getResource("source3").getPath();
		File source1 = new File(file1);
		File source2 = new File(file2);
		File source3 = new File(file3);
		List<UrlRequestPacket> listResults = new LinkedList<>();
		listResults.add(generateFakeData(source1));
		listResults.add(generateFakeData(source2));
		listResults.add(generateFakeData(source3));
		return listResults;
	}

	public List<UrlRequestPacket> generateLatePack() throws IOException {
		String file4 = this.getClass().getClassLoader().getResource("source4").getPath();
		File source4 = new File(file4);
		List<UrlRequestPacket> listResults = new LinkedList<>();
		listResults.add(generateFakeData(source4));
		return listResults;
	}

	private UrlRequestPacket generateFakeData(File file) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
		List<URL> urlList = new LinkedList<>();
		String queueName = null;
		String host = null;
		int crawlDelay = 0;
		for (String line :
				lines) {
			String[] dafu = line.split(" ");
			queueName = dafu[1];
			crawlDelay = Integer.parseInt(dafu[3]);
			String url = dafu[5];
			host = getHost(url);
			urlList.add(new URL(url));
		}
		return new UrlRequestPacket(queueName, host, urlList, crawlDelay);
	}

	public static String getHost(String url) {
		int end = getHostEnd(url);
		int beg = getHostBeg(url, end);
		return url.substring(beg, end);
	}

	public static int getHostBeg(String url, int hostEnd) {
		int p0 = url.indexOf("://");
		if (p0 < 0) {
			throw new IllegalArgumentException("Url must contain protocol: " + url);
		}
		p0 += 3;
		int atPos = url.indexOf('@', p0);
		if (p0 <= atPos && atPos < hostEnd) {
			throw new IllegalArgumentException("Url must not contain userinfo: " + url);
		}
		return p0;
	}

	public static int getHostEnd(String url) {
		int p0 = url.indexOf("://");
		if (p0 < 0) {
			throw new IllegalArgumentException("Url must contain protocol: " + url);
		}
		p0 += 3;

		int hostEnd = url.indexOf('/', p0);
		if (hostEnd < 0) {
			hostEnd = url.length();
		}

		int queryPos = -1;
		int fragmentPos = -1;
		int atPos = -1;
		int portPos = -1;

		for (int i = p0; i < hostEnd; ++i) {
			char c = url.charAt(i);
			if (c >= 'a' && c <= 'z') {
				continue;
			}
			if (queryPos == -1 && c == '?') {
				queryPos = i;
			}

			if (fragmentPos == -1 && c == '#') {
				fragmentPos = i;
			}

			if (atPos == -1 && c == '@') {
				atPos = i;
			}

			if (portPos == -1 && c == ':') {
				portPos = i;
			}
		}

		if (p0 < queryPos && queryPos < hostEnd) {
			hostEnd = queryPos;
		} else if (queryPos == p0) {
			throw new IllegalArgumentException("Url must contain host: " + url);
		}

		if (fragmentPos > 0) {
			if (p0 < fragmentPos && fragmentPos < hostEnd) {
				hostEnd = fragmentPos;
			} else if (fragmentPos == p0) {
				throw new IllegalArgumentException("Url must contain host: " + url);
			}
		}

		if (p0 <= atPos && atPos < hostEnd) {
			throw new IllegalArgumentException("Url must not contain userinfo: " + url);
		}

		if (portPos > 0) {
			if (p0 < portPos && portPos < hostEnd) {
				hostEnd = portPos;
			} else if (portPos == p0) {
				throw new IllegalArgumentException("Url must contain host: " + url);
			}
		}
		return hostEnd;
	}
}
