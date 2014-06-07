package query;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.lemurproject.kstem.KrovetzStemmer;

import crawler.MainSettings;

/**
 * Constructs similarity by given query
 * @author Sarp
 *
 */
public class Word2VecHashConstructor {
	private static Word2VecDistance w2vDist;
	public static void Constructor(String query, int amount) {
		if (w2vDist == null) {//Make sure Word2Vec is initialised only once
			w2vDist = new Word2VecDistance(MainSettings.WORD2VEC_FILE_NAME);
		}
		LinkedHashMap<String, Double> results = w2vDist.getWordByScores(query, amount, 0.0);
		if (results == null) {
			return;
		}
		QueryStore qStore = QueryStore.getInstance();
		for (Entry<String, Double> result : results.entrySet()) {
			double entryValue = Math.pow(result.getValue(), 10.0);//^10 to have difference between results
			qStore.setTermVsTermAndScore(query, result.getKey(), entryValue);
			System.out.println (result.getKey() + " : " + entryValue);
		}
		
		//split and tokenise the query so that each of their entry would have 10 score
		KrovetzStemmer kStemmer = new KrovetzStemmer();
		for (String tok : query.split("[^a-zA-Z]+")) {
			qStore.setTermVsTermAndScore(query, kStemmer.stem(tok), MainSettings.EXACT_MATCH_SCORE);
		}
		
	}

}
