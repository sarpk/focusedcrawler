package downloader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;


/**
 * Main downloader class that drives downloading process
 * @author Sarp
 *
 */
public class MainDownloader {
	private Download download;
	private Parse parser;

	
	/**
	 * Constructor for the Main Downloader
	 * @param address to download the address
	 */
	public MainDownloader(String address) {
		download = new Download(address);
		if(didDownloadFinish()) {
			parser = new Parse (download.getPageContent());
		}
	}
	
	/**
	 * @return True if download is finished
	 */
	public boolean didDownloadFinish() {
		return download.didDownloadFinish();
	}
	
	/**
	 * @return Body of page
	 */
	public String getPageBody() {
		return parser.getPageBody();
	}
	
	/**
	 * @return Body of page as a String array
	 */
	public String[] getBodyArray() {
		return parser.getPageBodyArray();
	}
	

	
	/**
	 * @return Body of page as a String set for no duplicates
	 */
	public LinkedHashSet<String> getBodySet() {
		return parser.getPageBodySet();
	}
	
	/**
	 * @return Clickable(href) link map, key is URL, value is the content
	 */
	public LinkedHashMap<String, String> getClickableLinks() {
		return parser.getLinks();
	}
	
	public Map.Entry<Integer,ArrayList<String>> setClosestLinksToTerm(String term) {
		return parser.getElementsTermOccuranceWithLinks(term);
	}

	
}
