package query;

import java.util.LinkedHashMap;

/**
 * Singleton class that stores term vs term hash
 * @author Sarp
 */
public class QueryStore {
	private static QueryStore singleton = null;
	private LinkedHashMap<String, LinkedHashMap<String, Integer>> termHash;
	/**
	 * @return The instance of singleton
	 */
	public static synchronized QueryStore getInstance() {
		if (singleton == null) {
			singleton = new QueryStore();
		}
		return singleton;
	}
	
	/**
	 * @param term
	 * @return The hash of given term
	 */
	public LinkedHashMap<String, Integer> getTerms(String term) {
		return termHash.get(term);
	}
	
	/**
	 * Size of terms existing in given term
	 * @param term
	 * @return size of terms, 0 if term doesn't exist
	 */
	public Integer getTermsSize(String term) {
		LinkedHashMap<String, Integer> entry = termHash.get(term);
		if (entry != null) {
			return entry.size();
		}
		return 0;
	}
	
	/**
	 * Checks if given term exists in hash
	 * @param term
	 * @return true if term exists in hash
	 */
	public boolean termExists(String term) {
		LinkedHashMap<String, Integer> entry = termHash.get(term);
		if (entry != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param term1 - initial term
	 * @param term2 - term to be checked in term1
	 * @return True if term2 exists in term1, otherwise false
	 */
	public boolean termvsTermExists(String term1, String term2) {
		LinkedHashMap<String, Integer> entry = termHash.get(term1);
		if (entry != null) {
			if (entry.get(term2) != null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gives the score of term2 in term1
	 * @param term1
	 * @param term2
	 * @return score of term2 in term1, if doesn't exist 0
	 */
	public Integer getTermvsTermScore(String term1, String term2) {
		LinkedHashMap<String, Integer> entry = termHash.get(term1);
		if (entry != null) {
			Integer score = entry.get(term2);
			if (score != null) {
				return score;
			}
		}
		return 0;
	}
	
	/**
	 * Add the term vs term
	 * @param term1
	 * @param term2 is the term to be inside of term1
	 * @param score is the score of term2 for term1
	 */
	public void setTermVsTermAndScore(String term1, String term2, Integer score) {
		LinkedHashMap<String, Integer> entry = termHash.get(term1);
		if (entry == null) {
			entry = new LinkedHashMap<String, Integer>();
		}
		Integer tmpScore = entry.get(term2); 
		if (tmpScore == null) {//add the entry
			entry.put(term2, score);
		}
		termHash.put(term1, entry);
	}
	
	/**
	 * Private constructor
	 */
	private QueryStore(){
		termHash = new LinkedHashMap<String, LinkedHashMap<String, Integer>>();
	}

}
