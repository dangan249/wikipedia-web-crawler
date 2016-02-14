import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PageRank {
	private Multimap<String, String> inLinkMap;
	private Multimap<String, String> outLinkMap;
	private Set<String> allTitles;

	private int numIteration;
	private double DAMPING_FACTOR = 0.85;

	public PageRank(String inLinkFilePath) throws IOException {
		this.numIteration = 0;
		this.inLinkMap = getMapFromFile(inLinkFilePath);
		this.outLinkMap = buildOutLinkMap(inLinkMap);
	}

	private Multimap<String, String> buildOutLinkMap(Multimap<String, String>  inLinkMap) {
		Multimap<String, String> outLinkMap = LinkedHashMultimap.create();
		this.allTitles = new HashSet<String>();

		for(String node : inLinkMap.keys()) {
			allTitles.add(node);
			for(String inLink : inLinkMap.get(node)) {
				outLinkMap.put(inLink, node);
				allTitles.add(inLink);
			}
		}
		return outLinkMap;
	}

	private Multimap<String, String> getMapFromFile(String filePath) throws IOException {
		Multimap<String, String> inLinkMap = LinkedHashMultimap.create();
		BufferedReader br = parseInputFile(filePath);
		String line;
		while ((line = br.readLine()) != null) {
			String[] sourceAndInLinks = Iterables.toArray(Splitter.on(' ').trimResults().omitEmptyStrings().split(line), String.class);
			for(int i = 1; i < sourceAndInLinks.length; i++) {
				inLinkMap.put(sourceAndInLinks[0], sourceAndInLinks[i]);
			}
		}
		return inLinkMap;
	}

	private BufferedReader parseInputFile(String filePath) throws IOException {
		return new BufferedReader(new FileReader(filePath));
	}

	private Map<String, Double> rank() {
		Map<String, Double> scores = new HashMap<String, Double>();
		Map<String, Double> newScores = new HashMap<String, Double>();
		int numNodes = allTitles.size();
		Iterable<String> sinkNodes = Iterables.filter(allTitles, new Predicate<String>() {
			public boolean apply(String input) {
				return outLinkMap.get(input).size() == 0;
			}
		});

		// set Initial value
		for(String title : allTitles) {
			scores.put(title, 1 / (double)numNodes);
		}

		while(true) {
			// calculate total sink PR
			double sinkPR = 0.0;
			for(String sinkNode : sinkNodes) {
				sinkPR += scores.get(sinkNode);
			}

			for(String node : allTitles) {
				double newRank =  (1 - DAMPING_FACTOR) / numNodes;
				newRank += DAMPING_FACTOR * sinkPR / numNodes;

				for(String inLink: inLinkMap.get(node)) {
					newRank += DAMPING_FACTOR * scores.get(inLink) / outLinkMap.get(inLink).size();
				}
				newScores.put(node, newRank);
			}

			this.numIteration++;

			// copy new scores computed from previous iteration
			for(String node : allTitles) {
				scores.put(node, newScores.get(node));
			}

			if(isPageRankConverged(scores)) {
				break;
			}
		}

		return scores;
	}

	// check  the perplexity of the PageRank distribution to see if it is less than 1
	private boolean isPageRankConverged(Map<String, Double> newScores) {
		System.out.println(getPerplexity(newScores));
		return getPerplexity(newScores) < 1;
	}

	private double getPerplexity(Map<String, Double> distribution) {
		double sum = 0;
		for(String item : distribution.keySet()) {
			sum += (distribution.get(item) * (Math.log(distribution.get(item)) / Math.log(2)));
		}
		return Math.pow(2, -sum);
	}

	public static void main(String [] args) {
		PageRank ranker = null;
		try {
			ranker = new PageRank(args[0]);
			Map<String, Double> scores = ranker.rank();

			for(String node : scores.keySet()) {
				System.out.println(node + ": " + scores.get(node));
			}

		} catch (IOException e) {
			System.out.println("Invalid file.");
		}
	}
}
