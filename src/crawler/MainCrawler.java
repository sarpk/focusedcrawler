package crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.jsoup.Jsoup;

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
	
	private final Integer maxCrawlPage = 50;
	private ScorePriorityMap crawledLinks;
	private ScorePriorityMap highestScoredPages;
	
	public MainCrawler() {
		weightThreshold = 0;
		amountOfCrawledPage = 0;
		crawledLinks = new ScorePriorityMap();
		highestScoredPages = new ScorePriorityMap();
	}
	
	
	/**
	 * Runner method for crawler
	 * @param address
	 * @param query
	 */
	public void crawlerRunner(String address, String query) {
		crawl(address, query);//Initial run
		while (crawlHighest(query));//While can crawl
	}
	
	/**
	 * Prints top 10 results
	 * Don't print more than once!
	 */
	public void printTop10Pages(){
		for (int i = 1; i < 11; i++) {
			String highestAddr = getHighestScoredPages();
			if (highestAddr != null) {
				System.out.println(i + ": "+ highestAddr);
			}
			else {
				System.out.println("No link to print");
				break;
			}
		}
	}
	
	/**
	 * Crawls the given page
	 * @param address of the website
	 * @param query 
	 * @param termHash 
	 */
	private void crawl(String address, String query) {
		QueryStore qStore = QueryStore.getInstance();
		MainDownloader mDownloader = new MainDownloader(address);
		if (mDownloader.didDownloadFinish()) {
			System.out.println("Page is downloaded");
			Integer currentWeight = 0;
			LinkedHashMap<String,Double> localLinks = new LinkedHashMap<String,Double>();
			int splitTextSize = mDownloader.getParser().getSplitText().size();
			for (int i = 0; i < splitTextSize; i++) {
				String splitText = mDownloader.getParser().getSplitText().get(i);
				if (!splitText.contains("</a>")) {//Not href
			    	String content = splitText.trim();
			    	String[] textContents = content.split("[^a-zA-Z]+");
			    	for (int j = 0; j < textContents.length; j++ ) {
			    		String word = textContents[j];
			    		Integer wordScore = qStore.getTermvsTermScore(query, word.toLowerCase()); 
						if (wordScore != 0) {
							int getInd = -1;
							if((j >= textContents.length/2) && ((i+1) < splitTextSize ) &&
									mDownloader.getParser().getSplitText().get(i+1)
									.contains("</a>")) {
								getInd = i+1;
							}
							else if (((i-1) >= 0) &&
							mDownloader.getParser().getSplitText().get(i-1)
							.contains("</a>")) {
								getInd = i-1;
							}
							
							if (getInd != -1) {
								String hrefLink = 
										mDownloader.getParser().getSplitText().get(getInd);
								String hrefLinkAttr = 
										Jsoup.parse(hrefLink, "").select("a[href]").attr("href");
								//System.out.println(hrefLinkAttr);
								Double linkScore = (wordScore.doubleValue()/
										qStore.getAmountEntries(query).doubleValue()*0.9);
								System.out.println(word + ": " +linkScore);
								localLinks.put(hrefLinkAttr, linkScore);
							}
						}
						currentWeight += wordScore;
			    	}
				}
			}
			
			/*for (String word : mDownloader.getBodySet()) {
				Integer wordScore = qStore.getTermvsTermScore(query, word.toLowerCase()); 
				if (wordScore != 0) {
					Entry<Integer, ArrayList<String>> closeLinkEnt = 
							mDownloader.setClosestLinksToTerm(word);
					Double linkScore = (wordScore.doubleValue()/
							qStore.getAmountEntries(query).doubleValue()*0.9);
					System.out.println(word + ": " +linkScore);
					/*for (String lAddr : closeLinkEnt.getValue()) {
						//localLinks.put(lAddr, linkScore);
					}
					wordScore *= closeLinkEnt.getKey();
				}
				currentWeight += wordScore;
			}*/
			savePageLinks(address, localLinks, currentWeight);
			savePage(address, currentWeight);
			System.out.println(currentWeight);
			System.out.println("Continuing crawling");
			LinkedHashMap<String, String> map = mDownloader.getClickableLinks();
			if (map != null) {
				for (Entry<String, String> pair : map.entrySet()) {
					String[] linkContents = pair.getValue().split("[^a-zA-Z]+");
					Integer linkWeight = 0;
					for (String linkContent : linkContents) {
						linkWeight += qStore.getTermvsTermScore(query, 
								linkContent.toLowerCase());
					}
					if (linkWeight > 0) {
						saveLink(address, pair.getKey(), linkWeight.doubleValue()*currentWeight);
					}
					/*System.out.println(pair.getKey() + " val is: "
							+ pair.getValue());*/
				}
			}
			
		}
		else {
			System.out.println("Download is not finished");
		}
	}
	

	
	private boolean crawlHighest(String query) {
		String highestAddr = getHighestScoredLink();
		if (shouldStopCrawling()) {
			return false;
		}
		
		if (highestAddr != null) {
			crawl(highestAddr, query);
			return true;
		}
		else {
			System.out.println("No link to crawl");
			return false;
		}
	}
	
	private String getHighestScoredLink() {
		String highestAddr = crawledLinks.getHighestScoreAddress();
		if(highestAddr!= null) {
			System.out.println("Highest addr is: " + highestAddr);
		}
		return highestAddr;
	}
	
	private String getHighestScoredPages() {
		String highestAddr = highestScoredPages.getHighestScoreAddress();
		if(highestAddr!= null) {
			System.out.println("Highest page is: " + highestAddr);
		}
		return highestAddr;
	}
	
	private void savePageLinks(String prefix, LinkedHashMap<String, Double> localLinks,
			Integer currentWeight) {
		for (Entry<String,Double> linkEn: localLinks.entrySet()) {
			Double linkWeight = linkEn.getValue()*currentWeight;
			saveLink(prefix,linkEn.getKey(), linkWeight);
		}
	}
	/**
	 * @return true if crawler should stop crawling more
	 */
	private boolean shouldStopCrawling() {
		amountOfCrawledPage++;
		if (amountOfCrawledPage >= maxCrawlPage) {
			return true;
		}
		if (amountOfCrawledPage % 500 == 0) {
			System.out.println("Current crawled amount is: " + amountOfCrawledPage);
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
			System.out.println("Saving page: " + address);
			highestScoredPages.addAddress(address, currentWeight.doubleValue());
			pageWeight(currentWeight);
		}
	}
	
	private void saveLink(String prefix, String address, Double linkWeight) {
		if (!address.startsWith("http")) {
			URL url = null;
			try {
				url = new URL(prefix);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			if (url != null) {
				address = String.format("%s://%s%s", url.getProtocol() ,url.getHost(),address);
			}
		}
		System.out.println(address + " with the weight of " + linkWeight);
		crawledLinks.addAddress(address, linkWeight);
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
