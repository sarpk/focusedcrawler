package query;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import com.google.common.collect.Ordering;

import crawler.ValueComparableMap;

/**
 * distance.c implementation from word2vec Works ONLY with NON-BINARY files Java
 * has index limitation so it works max 2^31(2B) words
 * 
 * @author Sarp
 */
public class Word2VecDistance {
	private float[][] M;
	private int size;
	private LinkedHashMap<String, Integer> vocabs;

	public Word2VecDistance(String fileName) {
		init(fileName);
	}
	
	
	/**
	 * @param entry to search
	 * @param maxEl Amount of max top results
	 * @param minVal Min dist to consider (between -1 to 1)
	 * @return 
	 * 
	 */
	public LinkedHashMap<String, Double> getWordByScores(String entry, int maxEl, double minVal) {
		
		String[] tmpEntries = entry.split(" ");
		ArrayList<String> entries = new ArrayList<String>();

		for (int i = 0; i < tmpEntries.length; i++) {// check if entry exists
			Integer index = vocabs.get(tmpEntries[i]);
			if (index == null) {
				continue;
			}
			entries.add(tmpEntries[i]);
			System.out.println("Word " + tmpEntries[i] + " is at " + index);
		}
		float vec[] = new float[size];
		for (int i = 0; i < size; i++) { vec[i] = 0; }
		for (int i = 0; i < entries.size(); i++) {
			int index = vocabs.get(entries.get(i));
			for (int j = 0; j < size; j++) {
				vec[j] += M[index][j];
				//System.out.print(vec[j] + " ");
			}
			//System.out.println("");
		}
		double len = 0;
		for (int i = 0; i < size; i++) {
			len += vec[i] * vec[i];
		}
		len = Math.sqrt(len);
		for (int i = 0; i < size; i++) {
			vec[i] /= len;
		}
		TreeMap<String, Double> highestScores = new ValueComparableMap<String, Double>(
				Ordering.natural().reverse());
		for (Entry<String, Integer> eachEntryMap : vocabs.entrySet()) {
			for (int i = 0; i < entries.size(); i++) {
				if (eachEntryMap.getKey().equals(entries.get(i))) {
					continue;
				}
			}
			double dist = 0;
			for (int i = 0; i < size; i++) {
				dist += vec[i] * M[eachEntryMap.getValue()][i];
			}
			if (dist > minVal) {//insert only dist is larger than limit
				highestScores.put(eachEntryMap.getKey(), dist);
				if (highestScores.size() > maxEl) {// Remove the last exceeding element
					highestScores.remove(highestScores.lastEntry().getKey());
				}
			}
		}
		
		LinkedHashMap<String,Double> highestMapScores = new LinkedHashMap<String, Double>(highestScores);
		for (Entry<String, Double> eachEntryTree : highestMapScores.entrySet()) {
			System.out.println (eachEntryTree.getKey() + " : " + eachEntryTree.getValue());
		}
		
		return highestMapScores;
	}

	/**
	 * 
	 * @param fileName Non-binary filename
	 */
	private void init(String fileName) {
		File inFile = new File(fileName);
		Scanner sc = null;
		try {
			sc = new Scanner(inFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int words = sc.nextInt();
		size = sc.nextInt();
		vocabs = new LinkedHashMap<String, Integer>(words);
		M = new float[words][size];

		for (int i = 0; i < words; i++) {
			// vocab[i] = sc.next();
			vocabs.put(sc.next(), i);
			// System.out.println(vocab[i]);
			for (int j = 0; j < size; j++) {
				M[i][j] = sc.nextFloat();
			}
			double len = 0;
			for (int j = 0; j < size; j++) {
				len += M[i][j] * M[i][j];
			}
			len = Math.sqrt(len);
			for (int j = 0; j < size; j++) {
				M[i][j] /= len;
				//System.out.println(M[i][j]);
			}
		}
		sc.close();



	}
	
	
	public static void main(String[] args) {
		Word2VecDistance w2vDist = new Word2VecDistance("wiki.txt");
		String entry = "to the";
		w2vDist.getWordByScores(entry, 20, -1.0);
	}
}


