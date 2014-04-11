package crawler;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.lemurproject.kstem.KrovetzStemmer;

import query.QueryStore;
import downloader.MainDownloader;

public class ThreadCrawler implements Runnable {
	private ScorePriorityMap crawledLinks;
	private ThreadController tControl;
	private String query;
	private int threadNo;
	
	public ThreadCrawler(ScorePriorityMap sPMap, int threadNo, ThreadController threadController, String query) {
		crawledLinks = sPMap;
		tControl = threadController;
		this.query = query;
		this.threadNo = threadNo;
	}
	
	public void run() {
		String addr = getHighestScoredLink();
		//System.out.println("Thread no: " + this.threadNo + " is running");
		if (addr != null) {
			boolean result = crawl(addr);
			tControl.tReport(result, this);
		}else {
			tControl.tReport(false, this);
		}
		//System.out.println("Thread no: " + this.threadNo + " is finished");
	}
	
	public int getThreadNo() {
		return threadNo;
	}
	
	private String getHighestScoredLink() {
		String highestAddr = crawledLinks.getHighestScoreAddress();
		if(highestAddr != null) {
			//System.out.println("Highest addr is: " + highestAddr);
		}
		return highestAddr;
	}

	private boolean crawl(String address) {
		QueryStore qStore = QueryStore.getInstance();
		MainDownloader mDownloader = new MainDownloader(address);
		if (mDownloader.didDownloadFinish()) {
			KrovetzStemmer kStemmer = new KrovetzStemmer();
			//System.out.println("Page is downloaded");
			Integer currentWeight = 0;
			LinkedHashMap<String,Double> localLinks = new LinkedHashMap<String,Double>();
			int splitTextSize = mDownloader.getSplitTextAmount();
			for (int i = 0; i < splitTextSize; i++) {
				String splitText = mDownloader.getSplitText(i);
				if (!splitText.contains("</a>")) {//Not href
			    	String content = splitText.trim();
			    	String[] textContents = content.split("[^a-zA-Z]+");
			    	for (int j = 0; j < textContents.length; j++ ) {
			    		String word = kStemmer.stem(textContents[j]);
			    		Integer wordScore = qStore.getTermvsTermScore(query, word.toLowerCase()); 
						if (wordScore != 0) {
							int getInd = -1;
							if((j >= textContents.length/2) && ((i+1) < splitTextSize ) &&
									mDownloader.getSplitText(i+1).contains("</a>")) {
								getInd = i+1;
							}
							else if (((i-1) >= 0) &&
							mDownloader.getSplitText(i-1).contains("</a>")) {
								getInd = i-1;
							}
							
							if (getInd != -1) {
								String hrefLink = mDownloader.getSplitText(getInd);
								String hrefLinkAttr = 
										Jsoup.parse(hrefLink, "").select("a[href]").attr("href");
								//System.out.println(hrefLinkAttr);
								Double linkScore = (wordScore.doubleValue()/
										qStore.getAmountEntries(query).doubleValue()*0.9);
								//System.out.println(word + ": " +linkScore);
								localLinks.put(hrefLinkAttr, linkScore);
							}
						}
						currentWeight += wordScore;
			    	}
				}
			}
			tControl.savePageLinks(address, localLinks, currentWeight, mDownloader);
			tControl.savePage(address, currentWeight);
			//System.out.println(currentWeight);
			LinkedHashMap<String, String> map = mDownloader.getClickableLinks();
			if (map != null) {
				for (Entry<String, String> pair : map.entrySet()) {
					String[] linkContents = pair.getValue().split("[^a-zA-Z]+");
					Integer linkWeight = 0;
					for (String linkContent : linkContents) {
						linkWeight += qStore.getTermvsTermScore(query, 
								kStemmer.stem(linkContent));
					}
					if (linkWeight > 0) {
						tControl.saveLink(address, pair.getKey(), linkWeight.doubleValue()*currentWeight);
					}
					/*System.out.println(pair.getKey() + " val is: "
							+ pair.getValue());*/
				}
			}
			return true;
		}
		else {
			System.out.println(String.format("Not downloaded: %s",  address));
			return false;
		}
	}

}
