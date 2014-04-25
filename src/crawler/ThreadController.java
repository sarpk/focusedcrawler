package crawler;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
	private int weightThreshold;
	private Semaphore reportSem;
	private long startTime;
	
	public ThreadController(String query, long startTime) {
		this.bucketSize = MainSettings.THREAD_AMOUNT;
		this.maxCrawlAmount = MainSettings.PAGE_AMOUNT;
		currentCrawled = 0;
		weightThreshold = 0;
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
			Integer currentWeight, MainDownloader mDownloader) {
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
	public void savePage(String address, Integer currentWeight) {
		if (currentWeight >= weightThreshold) {
			System.out.println("Saving page: " + address);
			highestScoredPages.addAddress(address, currentWeight.doubleValue());
			pageWeight(currentWeight);
		}
	}

	
	public void saveLink(String prefix, String address, Double linkWeight) {
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
		//System.out.println(address + " with the weight of " + linkWeight);
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
				printTop10Pages();
			}
		}else {
			runFreeThreads();
		}
		//System.out.println("Semaphore is released!");
		
		reportSem.release();
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
	 * @param weight
	 */
	private void pageWeight(Integer weight) {
		weightThreshold = Math.max(weightThreshold, weight/2);
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
		long endTime = System.currentTimeMillis();
	    System.out.println("Total execution time: " + (endTime-startTime)/1000 + " sec"); 
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
