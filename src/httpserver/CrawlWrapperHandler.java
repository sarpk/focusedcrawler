package httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class CrawlWrapperHandler implements HttpHandler {

	public void handle(HttpExchange exchange) {
		String requestMethod = exchange.getRequestMethod();
		if (requestMethod.equalsIgnoreCase("GET")) {
			String warning = "";
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
					String sentMsg = userInput[0].split("=")[1];
				} catch (Exception ex) {
					warning = "Message doesn't exist";
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

			writeResponse = top + authMsg + instrMsg + msgFill + bottom;
			
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