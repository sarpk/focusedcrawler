package crawler;

import java.util.HashSet;
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
		runCrawler();
	}
	
	public boolean runCrawler() {
		boolean result = false;
		String addr = getHighestScoredLink();
		//System.out.println("Thread no: " + this.threadNo + " is running");
		if (addr != null) {
			result = crawl(addr);
			tControl.tReport(result, this);
		}else {
			tControl.tReport(false, this);
		}
		//System.out.println("Thread no: " + this.threadNo + " is finished");
		return result;
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
			Double currentWeight = 0.0;
			LinkedHashMap<String,Double> localLinks = new LinkedHashMap<String,Double>();
			
			int splitTextSize = mDownloader.getSplitTextAmount();
			int tokenAmount = 0;
			HashSet<String> totalUniqueExactMatch = new HashSet<String>();//more unique exact match is better than total exact match
			for (int i = 0; i < splitTextSize; i++) {
				String splitText = mDownloader.getSplitText(i);
				if (!splitText.contains("</a>")) {//Not href
			    	String content = splitText.trim();
			    	String[] textContents = content.split("[^a-zA-Z0-9]+");
			    	tokenAmount += textContents.length;
		    		int prevWordInd = 0;
		    		String prevWord = null;
			    	for (int j = 0; j < textContents.length; j++ ) {
			    		String unstemmedWord = textContents[j].toLowerCase();
			    		String word = kStemmer.stem(unstemmedWord);
			    		Double wordScore = qStore.getTermvsTermScore(query, word);
			    		Double unstemmedScore = qStore.getTermvsTermScore(query, unstemmedWord);
			    		if ( unstemmedScore > wordScore) { //Ensure using the highest among stemmed and unstemmed
			    			wordScore = unstemmedScore;
			    			word = unstemmedWord;
			    		}
						if (wordScore != 0) {
							//Get the closest href link index
							int getInd = -1;
							if((j >= textContents.length/2) && ((i+1) < splitTextSize ) &&
									mDownloader.getSplitText(i+1).contains("</a>")) {
								getInd = i+1;
							}
							else if (((i-1) >= 0) &&
							mDownloader.getSplitText(i-1).contains("</a>")) {
								getInd = i-1;
							}
							
							//If closest href exists
							if (getInd != -1) {
								String hrefLink = mDownloader.getSplitText(getInd);
								String hrefLinkAttr = 
										Jsoup.parse(hrefLink, "").select("a[href]").attr("href");
								//System.out.println(hrefLinkAttr);
								Double linkScore = (wordScore.doubleValue() * qStore.getMinTermExponent(query));
								localLinks.put(hrefLinkAttr, linkScore);
							}
							
							//Boost the page score for the exact matchings
							//System.out.println("The wordScore is " + wordScore + " for the word " + word + " unstemmed as " + textContents[j]);
							if (wordScore == MainSettings.EXACT_MATCH_SCORE) {//If there is an exact match
								totalUniqueExactMatch.add(word);
								int dist = j - prevWordInd;
								//System.out.println("Trying wordscore with the dist of " + dist + " with the word " + word + " for addr " + address);
								if (dist < 10 && dist > 0 && prevWord != null && !prevWord.equals(word)) { //proximity of 10
									wordScore = Math.pow(wordScore, wordScore/dist);
									System.out.println("Wordscore for " + word + " w/ prev word " + prevWord + " is changed to " + wordScore + " for the addr of " + address);
								}
								prevWord = word;
								prevWordInd = j;
							}

						}
						currentWeight += wordScore;
			    	}
				}
			}
			currentWeight = Math.pow(currentWeight,//power it by the unique match
					Integer.valueOf(Math.max(totalUniqueExactMatch.size(), 1)).doubleValue()); //if there is no "unique" match then make sure it's powered by 1
			if (address.endsWith("/")) { address = address.substring(0, address.length()-1);}
			System.out.println("The address " + address + " is saved with the value of " + currentWeight + " w/TokAm " + currentWeight/tokenAmount);
			tControl.savePageLinks(address, localLinks, currentWeight, mDownloader);
			// Normalise currentWeight by the total tokens
			tControl.savePage(address, currentWeight/tokenAmount);
			//System.out.println(currentWeight);
			LinkedHashMap<String, String> map = mDownloader.getClickableLinks();
			if (map != null) {
				for (Entry<String, String> pair : map.entrySet()) {
					String[] linkContents = pair.getValue().split("[^a-zA-Z]+");
					Double linkWeight = 0.0;
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
