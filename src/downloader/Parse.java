package downloader;

import java.util.LinkedHashMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class parses the downloaded page content
 * @author Sarp
 *
 */
public class Parse {
	private Document document;
	private LinkedHashMap<String, String> linkMap;
	
	/**
	 * Constructor gets the document to set values
	 * @param doc Document to be parsed
	 */
	Parse(Document doc) {
		document = doc;
		setLinks();
	}
	
	/**
	 * 
	 * @return The page body in String
	 */
	public String getPageBody() {
		return document.body().text();
	}
	
	/**
	 * 
	 * @return The map of href, key is the URL and value is the content
	 */
	public LinkedHashMap<String, String> getLinks() {
		return linkMap;
	}
	
	private void setLinks() {
		LinkedHashMap<String, String> linkMap = new LinkedHashMap<String, String>();
		Elements links = document.select("a[href]");
		for (Element link : links) {
			// Store values from href attribute
			linkMap.put(link.attr("href"), link.text());
		}
	}
}
