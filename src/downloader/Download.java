package downloader;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * This class downloads the given address
 * @author Sarp
 *
 */
public class Download {
	private boolean downloadSt;
	private Document pageDoc;
	/**
	 * Constructor, which starts download process
	 * @param address- is address to be downloaded
	 */
	Download(String address) {
		downloadSt = false;
		downloadSt = download(address);
	}
	
	/**
	 * @return the content of page, null if download is not finished or corrupted
	 */
	public Document getPageContent() {
		if (didDownloadFinish()) {
			return pageDoc;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Checks the download status
	 * @return true if download is finished
	 */
	public boolean didDownloadFinish() {
		return downloadSt;
	}
	
	private boolean download(String address) {
		Connection jAddr = null; 
		try {			
			jAddr = Jsoup.connect(address);
		}
		catch (Exception e) {
			return false;
		}
		try {
			pageDoc = jAddr.get();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
