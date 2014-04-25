package httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.lemurproject.kstem.KrovetzStemmer;

import query.QueryStore;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import crawler.ThreadController;

public class CrawlSessionHandler implements HttpHandler {

	public void handle(HttpExchange exchange) {
		//System.out.println("handling");
		String requestMethod = exchange.getRequestMethod();
		if (requestMethod.equalsIgnoreCase("GET")) {
			String warning = null;
			String crawlId = null;
			ThreadController tControl = null;
			String links = null;
			String bMark = null;
			if (exchange.getRequestURI().getQuery() != null) {
				try {
					crawlId = URLDecoder.decode(exchange.getRequestURI()
							.getRawQuery(), "UTF-8");
					TControlSessions tCSessions = TControlSessions.getInstance();
					tControl = tCSessions.getController(crawlId);
					StringBuilder strB = new StringBuilder();
					int maxRes = Math.min(10, tControl.getHighestSavedPages().size());
					for (int i = 0; i < maxRes; i++) {
						String pLink = tControl.getHighestSavedPages().get(i);
						strB.append(String.format("<p>%d : <a href=\"%s\" target=\"_blank\">%s</a> \n</p>",(i+1),pLink, pLink));
					}
					links = strB.toString();
					bMark = String.format("\n<p>%d seconds are elapsed!</p>", tControl.getElapsedTime());
				} catch (Exception e) {
					warning = e.toString();
					e.printStackTrace();
				}
			}
			String refresh = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"10\">";
			String top = "<!DOCTYPE html>\n<html>\n<body>\n";
			String bottom = "\n<p><b>Note:</b> Done for Focused Crawler Project - Sarp.</p>\n"
					+ "\n</body>\n</html>\n";
			String backMessage = "<script>function goBack(){window.history.back()}</script>"
					+ "<input type=\"button\" value=\"Back\" onclick=\"goBack()\">"
					+ "<p><b>Warning: "
					+ warning
					+ "</b></p><br>";
			String authMsg = "\n<p>Welcome to my Focused Crawler HTTP Wrapper.</p>\n";			
			String instrMsg = "\n<p>Below are the top 10 search results for your query.</p>\n";
			String searchAgain = "<FORM METHOD=\"LINK\" ACTION=\"/\">" 
			+ "<INPUT TYPE=\"submit\" VALUE=\"Search another query/link\"></FORM>";
			String writeResponse = null;
			if (warning == null) {
				if (!tControl.isSearchStopped()) {
					top += refresh;
				}
				writeResponse = top  + authMsg + instrMsg 
						+ links + bMark + searchAgain +  bottom;
			}
			else {
				writeResponse = top + backMessage + bottom;
			}
			try {
				exchange.sendResponseHeaders(200, writeResponse.length());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OutputStream os = exchange.getResponseBody();
			try {
				os.write(writeResponse.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}