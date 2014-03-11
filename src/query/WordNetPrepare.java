package query;

import java.io.File;
import java.io.IOException;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;

/**
 * Prepares WordNet
 * @author Sarp
 */
public class WordNetPrepare {
	private final String WORDNET_DIR = "wordnet.database.dir";
	private IDictionary dictionary;

	/**
	 * Constructor that prepares WordNet
	 */
	public WordNetPrepare() {
		String OS = System.getProperty("os.name");
		if (OS.toLowerCase().startsWith("windows")) {
			System.setProperty(WORDNET_DIR,
					"C:\\Program Files (x86)\\WordNet\\2.1\\dict");
		} else {
			System.setProperty(WORDNET_DIR, "/usr/share/wordnet/");
		}
		File dicPath = new File(System.getProperty(WORDNET_DIR));
		// construct the dictionary object and open it
		dictionary = new Dictionary(dicPath);
		try {
			dictionary.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return The dictionary that has been prepared
	 */
	public IDictionary getDictionary() {
		return dictionary;
	}

}
