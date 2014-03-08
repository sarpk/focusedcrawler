package downloader;

import java.util.LinkedHashMap;

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
	 * @return Clickable(href) link map, key is URL, value is the content
	 */
	public LinkedHashMap<String, String> getClickableLinks() {
		return parser.getLinks();
	}
	
}
