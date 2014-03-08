package downloader;

import java.io.IOException;

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
	public Document pageContent() {
		if (didDownloadFinish()) {
			return pageDoc;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Checks the download status, returns true if download is finished
	 * @return
	 */
	public boolean didDownloadFinish() {
		return downloadSt;
	}
	
	private boolean download(String address) {
		try {
			pageDoc = Jsoup.connect(address).get();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
