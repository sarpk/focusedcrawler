package crawler;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import query.QueryStore;

import downloader.MainDownloader;

/**
 * Main Crawler Class that drives the crawler
 * 
 * @author Sarp
 * 
 */
public class MainCrawler {

	private Integer weightThreshold;
	private Integer amountOfCrawledPage;
	
	private final Integer maxCrawlPage = 10;
	
	public MainCrawler() {
		weightThreshold = 0;
		amountOfCrawledPage = 0;
	}
	/**
	 * Crawls the given page
	 * @param address of the website
	 * @param query 
	 * @param termHash 
	 */
	public void crawl(String address, String query) {
		QueryStore qStore = QueryStore.getInstance();
		MainDownloader mDownloader = new MainDownloader(address);
		if (mDownloader.didDownloadFinish()) {
			Integer currentWeight = 0;
			for (String word : mDownloader.getBodyArray()) {
				currentWeight += qStore.getTermvsTermScore(query, word.toLowerCase());
				savePage(address, currentWeight);
			}
			System.out.println(currentWeight);
			if (shouldStopCrawling()) {
				return;
			}
			System.out.println("Continuing crawling");
			LinkedHashMap<String, String> map = mDownloader.getClickableLinks();
			if (map != null) {
				for (Entry<String, String> pair : map.entrySet()) {
					String[] linkContents = pair.getValue().split("[^a-zA-Z]+");
					Integer linkWeight = 0;
					for (String linkContent : linkContents) {
						linkWeight += qStore.getTermvsTermScore(query, linkContent.toLowerCase());
					}
					if (linkWeight > 0) {
						saveLink(pair.getKey(), linkWeight);
					}
					/*System.out.println(pair.getKey() + " val is: "
							+ pair.getValue());*/
				}
			}
			
		}
	}
	
	/**
	 * 
	 * @return true if crawler should stop crawling more
	 */
	private boolean shouldStopCrawling() {
		amountOfCrawledPage++;
		if (amountOfCrawledPage >= maxCrawlPage) {
			return true;
		}
		return false;
	}
	
	/**
	 * Saves the page by the given weight
	 * @param address
	 * @param currentWeight
	 */
	private void savePage(String address, Integer currentWeight) {
		if (currentWeight >= getWeightThreshold()) {
			//TO-DO Save page with weight in a priority queue
			pageWeight(currentWeight);
		}
	}
	
	private void saveLink(String address, Integer linkWeight) {
		//TO-DO Save page with weight in a priority queue
		System.out.println(address + " with the weight of " + linkWeight);
	}
	
	/**
	 * @return The weight threshold
	 */
	private Integer getWeightThreshold() {
		return weightThreshold;
	}
	
	/**
	 * Sets the weight threshold
	 * @param weight
	 */
	private void pageWeight(Integer weight) {
		weightThreshold = Math.max(weightThreshold, weight/2);
	}
}
