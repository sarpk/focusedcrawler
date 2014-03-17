package query;

import java.util.List;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
/**
 * Class constructs a hashmap from given keyword by using WordNet
 * @author Sarp
 */
public class WordNetHashConstructor {
	//LinkedHashMap<String, Integer> stringHash;
	QueryStore qStore;
	
	/**
	 * Constructor
	 * @param dictionary 
	 * @param keyword to be constructed
	 */
	public WordNetHashConstructor(IDictionary dictionary, String keyword) {
		//stringHash = new LinkedHashMap<String, Integer>();
		qStore = QueryStore.getInstance();
		setSynonyms(dictionary, keyword, POS.ADJECTIVE);
		setSynonyms(dictionary, keyword, POS.ADVERB);
		setSynonyms(dictionary, keyword, POS.NOUN);
		setSynonyms(dictionary, keyword, POS.VERB);

	}

	/**
	 * @return term and score hashmap
	 */
	/*public LinkedHashMap<String, Integer> getHashMap() {
		return stringHash;
	}*/

	private void setSynonyms(IDictionary dict, String expandingWord, POS posType) {

		IIndexWord idxWord = null;
		try {
			idxWord = dict.getIndexWord(expandingWord, posType);
		} catch (Exception ex) {
			return;
		}
		List<IWordID> wordIDs = null;
		try {
			wordIDs = idxWord.getWordIDs();
		} catch (NullPointerException ex) {
			// skip if the pos type couldn't find anything
			return;
		}
		int score = 0;
		IWordID firstWordID = wordIDs.get(0);
		IWord firstword = dict.getWord(firstWordID);
		for (IWordID wordID : wordIDs) {
			IWord word = dict.getWord(wordID);
			ISynset synset = word.getSynset();
			// iterate over words associated with the synset
			for (IWord w : synset.getWords()) {
				if (!firstword.getLemma().equals(w.getLemma())) {
					if (w.getLemma().matches("^[a-zA-Z]+$")) {// make sure it's
																// all one word
						//stringHash.put(w.getLemma(), ++score);
						qStore.setTermVsTermAndScore(expandingWord, w.getLemma(), ++score);
					}
				}
			}
		}
	}
}
