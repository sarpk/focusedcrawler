package crawler;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import downloader.MainDownloader;

/**
 * Main Crawler Class that drives the crawler
 * 
 * @author Sarp
 * 
 */
public class MainCrawler {

	/**
	 * Crawls the given page
	 * @param address of the website
	 */
	public void crawl(String address) {
		MainDownloader mDownloader = new MainDownloader(address);
		if (mDownloader.didDownloadFinish()) {
			System.out.println(mDownloader.getPageBody() + "\nLinks are:\n");
			LinkedHashMap<String, String> map = mDownloader.getClickableLinks();
			if (map != null) {
				for (Entry<String, String> pair : map.entrySet()) {
					System.out.println(pair.getKey() + " val is: "
							+ pair.getValue());
				}
			}
		}
	}
}
