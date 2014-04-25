package httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpServer;

public class MainWebController {
	private Integer PORT_NO = 8001;
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
	
	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
}
