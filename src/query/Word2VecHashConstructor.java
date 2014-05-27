package query;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import crawler.MainSettings;

/**
 * Constructs similarity by given query
 * @author Sarp
 *
 */
public class Word2VecHashConstructor {
	
	public static void Constructor(String query, int amount) {
		Word2VecDistance w2vDist = new Word2VecDistance(MainSettings.WORD2VEC_FILE_NAME);
		LinkedHashMap<String, Double> results = w2vDist.getWordByScores(query, amount, 0.0);
		if (results == null) {
			return;
		}
		QueryStore qStore = QueryStore.getInstance();
		for (Entry<String, Double> result : results.entrySet()) {
			qStore.setTermVsTermAndScore(query, result.getKey(), result.getValue());
			System.out.println (result.getKey() + " : " + result.getValue());
		}
	}

}
