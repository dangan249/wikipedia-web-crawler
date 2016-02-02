import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class FocusCrawler extends Crawler {
	private URI seedURL;
	private String keyWord;

	public FocusCrawler(URI seedURL, String keyword) {
		this.seedURL = seedURL;
		this.keyWord = keyword;
	}

	public void bfsCrawl() throws IOException, InterruptedException {
		crawl(seedURL, false);
	}

	public void dfsCrawl() throws IOException, InterruptedException {
		Set<URI> visitedURIs = new HashSet<URI>();
		Set<URI> matchedURIs = new LinkedHashSet<URI>();

		dfsCrawlLimitedDepth(seedURL, seedURL, 1, visitedURIs, matchedURIs);
		saveURIsToFile(matchedURIs);
	}

	private void dfsCrawlLimitedDepth(URI uri, URI parentUri, int depth, Set<URI> visitedURIs, Set<URI> matchedURIs) throws IOException, InterruptedException {
		Document page = getPage(uri);
		visitedURIs.add(uri);
		if (isMatchedPage(page, uri)) {
			matchedURIs.add(uri);
		}

		if (visitedURIs.size() % 100 == 0) {
			System.out.println("Num page visited: " + visitedURIs.size());
		}

		if (depth == MAX_DEPTH || matchedURIs.size() == MAX_URLS_NUM) return;

		Set<URI> childURIs = getValidUniqueUris(page);
		for(URI childURI: childURIs) {
			if(visitedURIs.contains(childURI)) continue;

			// respect the politeness policy
			Thread.sleep(1000);

			dfsCrawlLimitedDepth(childURI, uri, depth + 1, visitedURIs, matchedURIs);
			if (matchedURIs.size() == MAX_URLS_NUM) return;
		}
	}

	public boolean isMatchedPage(Document page, URI uri) {
		if (uri.toString().contains(keyWord)) return true;

		// match keyword against page's anchor text
		Elements anchors = getPageAnchorElements(page);
		for (Element anchor : anchors) {
			if (anchor.text().contains(keyWord)) return true;
		}

		return page.text().contains(keyWord);
	}

	public static void main(String [] args) {
		FocusCrawler crawler = new FocusCrawler(SEED_URI, "solar");
		try {
			crawler.dfsCrawl();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
