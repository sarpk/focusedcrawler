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

public class SearchReqHandler implements HttpHandler {

	public void handle(HttpExchange exchange) {
		String requestMethod = exchange.getRequestMethod();
		if (requestMethod.equalsIgnoreCase("GET")) {
			String warning = null;
			String URL = null;
			String query = null;
			if (exchange.getRequestURI().getQuery() != null) {
				String httpQuery = null;
				try {
					httpQuery = URLDecoder.decode(exchange.getRequestURI()
							.getRawQuery(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					String userInput[] = httpQuery.split("&");
					query = userInput[0].split("=")[1];
					URL = userInput[1].split("=")[1];
					System.out.println(query);
					System.out.println(URL);
				} catch (Exception ex) {
					if (query == null) {
						warning = "Wrong query entry";
					} else {
						warning = "Wrong address entry";
					}
				}
			}
			
			if (query != null && URL != null) {
				QueryStore qStore = QueryStore.getInstance();
				KrovetzStemmer kStemmer = new KrovetzStemmer();
				
				query = kStemmer.stem(query);
				if (qStore.getTermsSize(query) == 0) {
					warning = String.format("The query \"%s\" doesn't exist please change your query", query);
				}
				
				long startTime = System.currentTimeMillis();
				System.out.println("Crawling: " + URL);
				ThreadController tControl = new ThreadController(query, startTime);
				if (!tControl.initialStart(URL)) {
					warning = String.format("The address \"%s\" is not loaded", URL);
				}
			}
			
			
			
			
			
			String top = "<!DOCTYPE html>\n<html>\n<body>\n";
			String bottom = "\n<p><b>Note:</b> Done for Focused Crawler Project - Sarp.</p>\n"
					+ "\n</body>\n</html>\n";
			String backMessage = "<script>function goBack(){window.history.back()}</script>"
					+ "<input type=\"button\" value=\"Back\" onclick=\"goBack()\">"
					+ "<p><b>Warning: "
					+ warning
					+ "</b></p><br>";
			
			String authMsg = "\n<p>Welcome to my Focused Crawler HTTP Wrapper.</p>\n"
					+ "\n</body>\n</html>\n";
			
			String instrMsg = "\n<p>Please enter the query to be searched and the seed page.</p>\n"
					+ "\n</body>\n</html>\n";
			
			
			String msgFill = "<br><form name=\"input\" action=\"/searchReq\" method=\"get\">"
					+ "Query: <input type=\"text\" name=\"query\" value=\"Technology\" ><br>"
					+ "Seed Address: <input type=\"text\" name=\"address\" value=\"http://www.qut.edu.au\"><br>"
					+ "<p><input type=\"submit\" value=\"Search\" /></p></form>";
			


			String writeResponse = null;

			if (warning == null) {
				writeResponse = top + authMsg + instrMsg + msgFill + bottom;
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