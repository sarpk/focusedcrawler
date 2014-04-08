package downloader;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;


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
		if(didDownloadFinish() && download.getPageContent() != null ) {
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
		return parser.getPageBodySet() != null ? parser.getPageBodySet() : new LinkedHashSet<String>();
	}
	
	/**
	 * @return Clickable(href) link map, key is URL, value is the content
	 */
	public LinkedHashMap<String, String> getClickableLinks() {
		return parser.getLinks();
	}
	

	/**
	 * 
	 * @return The amount of text that was split
	 */
	public int getSplitTextAmount() {
		int splitTextSize = 0;
		if (parser.getSplitText() != null) {  
			splitTextSize = parser.getSplitText().size();
		}
		return splitTextSize;
	}
	
	/**
	 * 
	 * @param index of split text
	 * @return given index of split, if not existing empty String
	 */
	public String getSplitText(int index) {
		String returnText = "";
		if (parser.getSplitText() != null) {  
			returnText = parser.getSplitText().get(index);
		}
		return returnText;
	}
	
	/**
	 * Parser wrapper for anchorLinkHandle
	 * @param anchorLink
	 * @return
	 */
	public LinkedHashSet<String> getExternalLinksFromAnchor(String anchorLink) {
		return parser.anchorlinkHandle(anchorLink);
	}
		
	
}
