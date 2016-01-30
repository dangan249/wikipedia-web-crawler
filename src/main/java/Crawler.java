import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Crawler {
	private static final int POLITENESS_DELAY = 1;
	private static final URI SEED_URL = URI.create("http://en.wikipedia.org/wiki/Sustainable_energy");
	private static final String BASE_URL = "http://en.wikipedia.org/";
	private static final String ENGLISH_WIKI_LINK_PREFIX = "en.wikipedia.org/wiki";
	private static final int MAX_DEPTH = 5;
	private static final int MAX_URLS_NUM = 1000;
	private static final HttpClient client = HttpClientBuilder.create().build();

	public void crawl() throws IOException {
		HttpGet request = new HttpGet(SEED_URL);
		HttpResponse response = client.execute(request);
		ArrayList<URI> urls = getUrls(response);
		ArrayList<String> frontier = new ArrayList<String>();
		for (URI url : urls) {
			if (isNonEnglishWikiLink(url) || isFragmentLink(url) || isAdministrativeLink(url)) {
				continue;
			}
			System.out.println(url);
			//frontier.add(trimUrl(url));
		}

		System.out.println("Response Code : "
				+ response.getStatusLine().getStatusCode());

		File file = new File("/Users/andang/IdeaProjects/web-crawler/downloaded/abcd.html");
		Files.copy(response.getEntity().getContent(), file.toPath());
	}

	// Add URLs and get keys
	private ArrayList<URI> getUrls(HttpResponse response) throws IOException {
		Document doc = Jsoup.parse(response.getEntity().getContent(), null, BASE_URL);
		Elements links = doc.select("a[href]");
		ArrayList<URI> urls = new ArrayList<URI>();
		for (Element link : links) {
			try {
				String url = link.attr("abs:href");
				urls.add(new URI(url));
			} catch (URISyntaxException e) {
				System.out.println("Bad url: " + urls.toString());
				continue;
			}
		}
		return urls;
	}

	private String trim(String s, int width) {
		if (s.length() > width)
			return s.substring(0, width-1) + ".";
		else
			return s;
	}

	private boolean isFragmentLink(URI url) {
		return url.toString().contains("#");
	}

	private boolean isAdministrativeLink(URI url) {
		return url.getPath().contains(":");
	}

	private boolean isNonEnglishWikiLink(URI url) {
		return !url.toString().contains(ENGLISH_WIKI_LINK_PREFIX);
	}

	public static void main(String [] args) {
		Crawler crawler = new Crawler();
		try {
			crawler.crawl();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
