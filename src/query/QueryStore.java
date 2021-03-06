package query;

import java.util.LinkedHashMap;

import org.lemurproject.kstem.KrovetzStemmer;

/**
 * Singleton class that stores term vs term hash
 * @author Sarp
 */
public class QueryStore {
	private static QueryStore singleton = null;
	private LinkedHashMap<String, LinkedHashMap<String, Double>> termHash;
	private LinkedHashMap<String, Double> minScores;
	
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
	public LinkedHashMap<String, Double> getTerms(String term) {
		return termHash.get(term);
	}
	
	/**
	 * Size of terms existing in given term
	 * @param term
	 * @return size of terms, 0 if term doesn't exist
	 */
	public Integer getTermsSize(String term) {
		LinkedHashMap<String, Double> entry = termHash.get(term);
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
		LinkedHashMap<String, Double> entry = termHash.get(term);
		if (entry != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param term1 - initial term
	 * @param term2 - term to be checked in term1
	 * @return True if term2 exists in term1 or term1 == term2, otherwise false
	 */
	public boolean termvsTermExists(String term1, String term2) {
		LinkedHashMap<String, Double> entry = termHash.get(term1);
		if (entry != null) {
			if (entry.get(term2) != null) {
				return true;
			}
		}
		else if (term1.equals(term2)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Gives the score of term2 in term1
	 * @param term1
	 * @param term2
	 * @return score of term2 in term1, if doesn't exist 0
	 */
	public Double getTermvsTermScore(String term1, String term2) {
		Double score = 0.0;
		LinkedHashMap<String, Double> entry = termHash.get(term1);
		if (entry != null) {
			score = entry.get(term2);
			if (score == null) {
				score = 0.0;
			}
		}
		if (term1.equals(term2)) {
			score = 1.0;//getAmountEntries(term1);
		}
		return score;
	}
	
	/**
	 * 
	 * @param term
	 * @return The total amount of entries for the given term
	 */
	public Double getAmountEntries(String term) {
		LinkedHashMap<String, Double> entry = termHash.get(term);
		if (entry != null) {
			return (entry.size()+2.0);
		}
		return 0.0;
	}
	
	/**
	 * Add the term vs term
	 * @param term1
	 * @param term2 is the term to be inside of term1
	 * @param score is the score of term2 for term1
	 */
	public void setTermVsTermAndScore(String term1, String term2, Double score) {
		KrovetzStemmer kStemmer = new KrovetzStemmer();
		term1 = kStemmer.stem(term1);
		term2 = kStemmer.stem(term2);
		LinkedHashMap<String, Double> entry = termHash.get(term1);
		if (entry == null) {
			entry = new LinkedHashMap<String, Double>();
		}
		
		Double dbl = minScores.get(term1);
		if (dbl == null) {
			dbl = 1.0;
		}
		
		Double tmpScore = entry.get(term2); 
		if (tmpScore == null) {//add the entry
			entry.put(term2, score);
		}
		
		if (dbl > 0.0 && dbl > score) {
			System.out.println("Min score is changing to " + score);
			minScores.put(term1, score);
		}
		
		termHash.put(term1, entry);
	}
	
	/**
	 * 
	 * @param query
	 * @return The lower base of the double number in log_10
	 */
	public double getMinTermExponent(String query) {
		return Math.pow(10, Math.floor(Math.log10(Math.abs(minScores.get(query)))));
	}
	
	/**
	 * Private constructor
	 */
	private QueryStore(){
		termHash = new LinkedHashMap<String, LinkedHashMap<String, Double>>();
		minScores = new LinkedHashMap<String, Double>();
	}

}
