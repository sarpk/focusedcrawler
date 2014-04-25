package httpserver;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map.Entry;

import crawler.ThreadController;

public class TControlSessions {
	private HashMap<String, ThreadController> idTControllerMap;
	private SecureRandom secRandom;
	private static TControlSessions instance;
	
	public static TControlSessions getInstance() {
		if (instance == null) {
			instance = new TControlSessions();
		}
		return instance;
	}
	
	public String newCrawler(String query) {
		long startTime = System.currentTimeMillis();
		ThreadController tControl = new ThreadController(query, startTime);
		String randStr = randomGenerator();
		idTControllerMap.put(randStr, tControl);
		return randStr;
	}
	
	public ThreadController getController(String identification) {
		return idTControllerMap.get(identification);
	}
	
	private String compareStr(String randomStr) {
		for (Entry<String, ThreadController> eachEntry : idTControllerMap.entrySet()) {
			if (randomStr.equals(eachEntry.getKey())) {
				randomStr = compareStr(new BigInteger(130, secRandom).toString(32));
			}
		}
		return randomStr;
	}
	
	private String randomGenerator() {
		return compareStr(new BigInteger(130, secRandom).toString(32));//recursively find unique key
	}
	
	private TControlSessions() {
		idTControllerMap = new HashMap<String, ThreadController>();
		secRandom = new SecureRandom();
	}

}
