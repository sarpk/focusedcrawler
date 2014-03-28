package downloader;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * This class parses the downloaded page content
 * @author Sarp
 *
 */
public class Parse {
	private Document document;
	private LinkedHashMap<String, String> linkMap;
	private String[] body;
	private LinkedHashSet<String> bodyWordsSet;
	
	/**
	 * Constructor gets the document to set values
	 * @param doc Document to be parsed
	 */
	public Parse(Document doc) {
		document = doc;
		setLinks();
		splitBodyToArrayAndSet();
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
	
	public Map.Entry<Integer,ArrayList<String>> getElementsTermOccuranceWithLinks(String term) {
		Elements elementsInTerm = document.getElementsContainingText(term);
		if (elementsInTerm == null) {
			return new AbstractMap.SimpleEntry<Integer, ArrayList<String>>(0, null);
		}
		Integer num = elementsInTerm.size();
		if (num == null) {
			return new AbstractMap.SimpleEntry<Integer, ArrayList<String>>(0, null);
		}
		return new AbstractMap.SimpleEntry<Integer, ArrayList<String>>
			(num, setClosestLinksInElements(elementsInTerm, term));
	}
	
	private ArrayList<String> setClosestLinksInElements(Elements elementsInTerm, String term) {
		ArrayList<String> links = new ArrayList<String>();   
		for (Element element : elementsInTerm) {
		    	Node nodeWithText = getFirstNodeContainingText(element.childNodes(), term);
		        Element closestLink = getClosestLink(nodeWithText, 0);
		        if (closestLink != null) {
		        	String foundLink = closestLink.attr("abs:href");
		        	//System.out.println("Link closest to '" + term + "': " + foundLink);
		        	links.add(foundLink);
		        }
		   }
		return links;
	}
	
	
	/*public void setClosestLinksToTerm(String term) {
	   for (Element element : document.getElementsContainingText(term)) {
		//Element element = document.getElementsContainingOwnText(term).first();
	    	Node nodeWithText = getFirstNodeContainingText(element.childNodes(), term);
	        Element closestLink = getClosestLink(nodeWithText, 0);
	        if (closestLink != null) {
	        	System.out.println("Link closest to '" + term + "': " + closestLink.attr("abs:href"));	
	        }	        
	    }
	}*/
	
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
	
	private Element getClosestLink(Node node, int depth) {
		if (depth > 20) {
			return null;
		}
		//System.out.println(depth);
	    Element linkElem = null;
	    if (node instanceof Element) {
	        Element element = (Element) node;
	        linkElem = element.getElementsByTag("a").first();
	    }
	    if (linkElem != null) {
	        return linkElem;
	    }

	    // This node wasn't a link. try next one
	    if (node == null) {
	    	return null;
	    }
	    
	    linkElem = getClosestLink(node.nextSibling(), depth+1);
	    if (linkElem != null) {
	        return linkElem;
	    }

	    // Wasn't next link. try previous
	    linkElem = getClosestLink(node.previousSibling(), depth+1);
	    if (linkElem != null) {
	        return linkElem;
	    }

	    return null;
	}

	private Node getFirstNodeContainingText(List<Node> nodes, String text) {
	    for (Node node : nodes) {
	        if (node instanceof TextNode) {
	            String nodeText = ((TextNode) node).getWholeText();
	            if (nodeText.contains(text)) {
	                return node;
	            }
	        }
	    }
	    return null;
	}
}
