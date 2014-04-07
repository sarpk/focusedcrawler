package query;

import downloader.MainDownloader;
import edu.mit.jwi.IDictionary;
/**
 * Example Dummy Hash Creator for WordNet
 * @author Sarp
 */
public class ExampleDocumentHandler {
	//private final String WIKI_RANDOM_PAGE = "http://en.wikipedia.org/wiki/Special:Random";
	private final String LONG_WIKI_ARTICLE = "http://en.wikipedia.org/wiki/2013_in_American_television";
	private String address;
	private IDictionary dictionary;
	//private LinkedHashMap<String, LinkedHashMap<String, Integer>> termHash;

	/**
	 * Constructor uses Wikipedia Random page to handle words
	 */
	public ExampleDocumentHandler() {
		//init(WIKI_RANDOM_PAGE);
		init(LONG_WIKI_ARTICLE);
		startHandling(0);
	}

	/**
	 * Constructor takes web page address to handle words
	 * @param address
	 */
	public ExampleDocumentHandler(String address) {
		init(address);
		startHandling(0);
	}
	
	/**
	 * @return Term to LinkedHashMap of term and score 
	 */
	/*public LinkedHashMap<String, LinkedHashMap<String, Integer>> getTermHash() {
		return termHash;
	}*/

	/**
	 * General initialiser
	 * @param address to be downloaded
	 */
	private void init(String address) {
		this.address = address;
		dictionary = new WordNetPrepare().getDictionary();
		//termHash = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
	}

	/**
	 * From the given website generates term vs term,score hashmap
	 * @param amount of times to be handled(useful for random pages)
	 */
	private void startHandling(int amount) {
		if (amount < 0) {
			return;
		}
		MainDownloader mDownloader = new MainDownloader(address);
		if (mDownloader.didDownloadFinish()) {
			handleBody(mDownloader.getBodyArray());
		}
		startHandling(--amount);
	}

	/**
	 * @param body of a webpage
	 */
	private void handleBody(String[] wordArr) {
		for (String eachWord : wordArr) {
			/*WordNetHashConstructor hashConst =*/
			new WordNetHashConstructor(dictionary, eachWord);
			//termHash.put(eachWord, hashConst.getHashMap());
		}
	}

}
