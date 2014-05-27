package httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class MainWebController {
	private Integer PORT_NO = 18810;
	public void setup() {
		try {
			/*String loginRes = readFile("htmlpages/loginpage.html",
					Charset.defaultCharset());
			String authRes = readFile("htmlpages/authpage.html",
					Charset.defaultCharset());*/
			
			HttpServer server = HttpServer.create(
					new InetSocketAddress(PORT_NO), 0);
			server.createContext("/", new CrawlWrapperHandler());
			server.createContext("/searchReq", new SearchReqHandler());
			server.createContext("/crawl", new CrawlSessionHandler());
			server.setExecutor(null); // creates a default executor
			server.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
