package downloader;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.jsoup.Jsoup;
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
	private Document xmpDocument;
	private LinkedHashMap<String, String> linkMap;
	private String[] body;
	private LinkedHashSet<String> bodyWordsSet;
	private List<String> splitText;
	private String splitter = "ASD1244FJ236KTJ2jlk235jkldsajSDG234sdG324DFG235SDG214aSdSDg123";
	
	/**
	 * Constructor gets the document to set values
	 * @param doc Document to be parsed
	 */
	public Parse(Document doc) {
		document = doc;
		setLinks();
		splitBodyToArrayAndSet();
		setXMPForHref();
	}
	
	/**
	 * 
	 * @return The page body in String
	 */
	public String getPageBody() {
		return document.body().text();
	}
	
	/**
	 * @return Body of page as a String array
	 */
	public String[] getPageBodyArray() {
		return body;
	}
	
	/**
	 * @return Body of page as a String set for no duplicates
	 */
	public LinkedHashSet<String> getPageBodySet() {
		return bodyWordsSet;
	}
	
	/**
	 * 
	 * @return The map of href, key is the URL and value is the content
	 */
	public LinkedHashMap<String, String> getLinks() {
		return linkMap;
	}
	
	/**
	 * 
	 * @return Splited text
	 */
	public List<String> getSplitText() {
		return splitText;
	}
	
	/**
	 * 
	 * @param anchorLink Given Anchorlink, must start with hashtag(#)
	 * @return Set of links in String
	 */
	public LinkedHashSet<String> anchorlinkHandle(String anchorLink) {
		LinkedHashSet<String> anchorLinks = new LinkedHashSet<String>();//Avoid duplicate links
		anchorLink = anchorLink.substring(1);//get rid off hashtag
		System.out.println("Handling anchorlink: " + anchorLink);
		Element anchorElement = document.getElementById(anchorLink);
		if (anchorElement == null) { return anchorLinks;}
		Elements links = anchorElement.select("a[href]");
		for (Element link : links) {
			// Store values from href attribute
			String attr = link.attr("href");
			if (!attr.startsWith("#")) {//Potential loop if another anchor link
				System.out.println("attr: " + attr);
				anchorLinks.add(attr);
			}
		}
		return anchorLinks;
	}
	
	private void setXMPForHref() {
		if (document == null || document.body() == null) {
			return;
		}
		String splitWrapper = String.format("<xmp>%s</xmp>%s", splitter,splitter);
		for (Element element : document.body().select("a[href]")) {
			element.wrap(splitWrapper); 
		}
		 
		xmpDocument = Jsoup.parse(document.html());
		//System.out.println(xmpDocument.body().text());
		splitText = Arrays.asList(xmpDocument.body().text().split(splitter));
	}
	
	
	private void setLinks() {
		linkMap = new LinkedHashMap<String, String>();
		Elements links = document.select("a[href]");
		for (Element link : links) {
			// Store values from href attribute
			String attr = link.attr("href");
			String content = link.text();

			linkMap.put(attr, content);
		}
	}
	
	private void splitBodyToArrayAndSet() {
		try {
			body =  getPageBody().split("[^a-zA-Z]+");
		}
		catch (Exception e) {return;}
		bodyWordsSet = new LinkedHashSet<String>();
		for (String bodyW : body) {
			bodyWordsSet.add(bodyW);
		}
	}
}
