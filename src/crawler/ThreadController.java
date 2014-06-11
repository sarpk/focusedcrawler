package crawler;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import downloader.MainDownloader;


public class ThreadController {
	private ArrayList <ScorePriorityMap> bucketToBeCrawled;
	private ArrayList <ThreadCrawler> crawlerThreads;
	private ArrayList <ThreadCrawler> freeThreads;
	private ScorePriorityMap highestScoredPages;
	private int bucketSize;
	private final int maxCrawlAmount;
	private int currentCrawled;
	private double weightThreshold;
	private Semaphore reportSem;
	private long startTime;
	private long lastTime;
	private boolean searchFinished;
	
	public ThreadController(String query, long startTime) {
		this.bucketSize = MainSettings.THREAD_AMOUNT;
		this.maxCrawlAmount = MainSettings.PAGE_AMOUNT;
		currentCrawled = 0;
		weightThreshold = Double.NEGATIVE_INFINITY;
		searchFinished = false;
		this.startTime = startTime;
		reportSem = new Semaphore(1);
		highestScoredPages = new ScorePriorityMap();
		bucketToBeCrawled = new ArrayList <ScorePriorityMap>();
		crawlerThreads = new ArrayList <ThreadCrawler>();
		freeThreads = new ArrayList <ThreadCrawler>();
		for (int i = 0; i < bucketSize; i++) {
			ScorePriorityMap sPMap = new ScorePriorityMap();
			bucketToBeCrawled.add(sPMap);
			ThreadCrawler tCrawl = new ThreadCrawler(sPMap, i+1, this, query);
			crawlerThreads.add(tCrawl);
			freeThreads.add(tCrawl);
		}
	}

	public boolean initialStart(String url) {
		int hashNum = hashString(url);
		ScorePriorityMap addressMap = bucketToBeCrawled.get(hashNum);
		addressMap.addAddress(url, 0.1);
		ThreadCrawler firstChosenThread = crawlerThreads.get(hashNum);
		freeThreads.remove(firstChosenThread);
		return firstChosenThread.runCrawler();  //start();
	}
	
	

	public void savePageLinks(String prefix, LinkedHashMap<String, Double> localLinks,
			Double currentWeight, MainDownloader mDownloader) {
		for (Entry<String,Double> linkEn: localLinks.entrySet()) {
			Double linkWeight = linkEn.getValue()*currentWeight;
			String address = linkEn.getKey();
			if (address.startsWith("#")) {//Anchorlink
				for (String anchorLinks : mDownloader.getExternalLinksFromAnchor(address)) {
					saveLink(prefix, anchorLinks, linkWeight);
				}
			}else {
				saveLink(prefix,address, linkWeight);
			}
		}
	}

	/**
	 * Saves the page by the given weight
	 * @param address
	 * @param currentWeight
	 */
	public void savePage(String address, Double currentWeight) {
		if (currentWeight >= weightThreshold) {
			System.out.println("Saving page: " + address);
			highestScoredPages.addAddress(address, currentWeight.doubleValue());
			pageWeight(currentWeight);
		}
		//The below condition is added for cases where there aren't enough results after first few pages
		else if (highestScoredPages.getMaxScore(10) <= currentWeight ) { // 10 results are always returned, so check that 
			System.out.println("Saving page: " + address);
			highestScoredPages.addAddress(address, currentWeight.doubleValue());
			pageWeight(currentWeight);
		}
		else {
			System.out.println("Rejected page: " + address);
			System.out.println("Highest score was: " + highestScoredPages.getMaxScore(10));
		}
	}

	
	public void saveLink(String prefix, String address, Double linkWeight) {
		System.out.println("Addr before host handle: " + address);
		if (!address.startsWith("http")) {
			if (!(address.startsWith("/") || address.startsWith("\\")) ) {
				address = String.format("/%s", address);
			}
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
		else {
			URL url = null;
			try {
				url = new URL(address);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			address = String.format("%s://%s%s", url.getProtocol() ,url.getHost(), url.getPath());
		}
		if (address.endsWith("/") || address.endsWith("\\")) {
			address = address.substring(0, address.length()-2);
		}
		System.out.println("Addr before hashtag handle: " + address);
		//Handle the hashtag
		address = address.split("#")[0];
		System.out.println(address + " with the weight of " + linkWeight);
		int bucketNo = hashString(address);
		bucketToBeCrawled.get(bucketNo).addAddress(address, linkWeight);
	}

	public void tReport(boolean crawl, ThreadCrawler threadCrawler) {

		if (crawl) {currentCrawled++;}
		/*if (!crawl) {//Don't bother if not crawled 
			if (freeThreads.size() == bucketSize) {
				printTop10Pages();
			}
			System.out.println("tReport returning" + " f t size " + freeThreads.size() + " bsize " + bucketSize );
			return;
		}*/
		//System.out.println("Trying to acquire semaphore");
		try {
			reportSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		freeThreads.add(threadCrawler);
		//System.out.println("permits are:  " + reportSem.availablePermits());
		if (currentCrawled >= maxCrawlAmount) {
			//System.out.println("Nothing to crawl");
			System.out.println("tReport trying to print" + " f t size " + freeThreads.size() + " bsize " + bucketSize );
			/*for (ThreadCrawler thread : freeThreads) {
				System.out.print( thread.getThreadNo() + " " );
			}
			System.out.println("");*/
			if (freeThreads.size() == bucketSize) {
				finishSearch();
				//printTop10Pages();
			}
		}else {
			runFreeThreads();
		}
		//System.out.println("Semaphore is released!");
		
		reportSem.release();
	}
	
	/**
	 * 
	 * @return If search is finished or not
	 */
	public boolean isSearchStopped() {
		return searchFinished;
	}
	
	/**
	 * 
	 * @return The elapsed time in seconds, does not increment after search result
	 */
	public long getElapsedTime() {
		long endTime = 0;
		if (searchFinished) {
			endTime = lastTime;
		} else {
			endTime = System.currentTimeMillis();
		}
		
	    return (endTime-startTime)/1000;
	}
	
	/**
	 * Returns Addresses without popping, safe to call multiple times
	 * @return Highest Scored Addresses
 	 */
	public List<String> getHighestSavedPages() {
		return highestScoredPages.getHighestScoreAddressesWithoutPopping();
	}
	
	private int hashString(String url) {
		URL urlDomain = null;
		try {
			urlDomain = new URL(url);
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			return 0;
		}
		String domain = urlDomain.getHost();		
		
		BigInteger hash=BigInteger.valueOf(7);
		for (int i=0; i < domain.length(); i++) {
		    hash = hash.multiply(BigInteger.valueOf(31));
		    hash = hash.add(BigInteger.valueOf(domain.charAt(i)));
		}
		//System.out.println("Hashing: " + domain + " for the value of: " + hash);
		return hash.mod(BigInteger.valueOf(bucketSize)).intValue();
	}
	
	/**
	 * Sets the weight threshold
	 * @param currentWeight
	 */
	private void pageWeight(Double currentWeight) {
		weightThreshold = Math.max(weightThreshold, (currentWeight/2.0));
	}
	
	/**
	 * Prints top 10 results
	 * Don't print more than once!
	 */
	private void printTop10Pages(){
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
		finishSearch();
	}
	
	private void finishSearch() {
		lastTime = System.currentTimeMillis();
		searchFinished = true;
	    System.out.println("Total execution time: " + getElapsedTime() + " sec"); 
	}
	
	private String getHighestScoredPages() {
		String highestAddr = highestScoredPages.getHighestScoreAddress();
		if(highestAddr!= null) {
			//System.out.println("Highest page is: " + highestAddr);
		}
		return highestAddr;
	}

	private void runFreeThreads() {
		ArrayList<ThreadCrawler> tmpThreads = new ArrayList<ThreadCrawler>(freeThreads);
		freeThreads.clear();
		//System.out.println("Before the loop");
		for (ThreadCrawler thread : tmpThreads) {
			//System.out.print(thread.getThreadNo() + " ");
			new Thread(thread).start();
		}
		//System.out.println("");
		//System.out.println("After the loop");
	}

}
