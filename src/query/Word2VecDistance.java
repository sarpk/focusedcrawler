package query;

import java.io.File;
import java.io.FileNotFoundException;
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
	private static float[][] M;
	private static int size;
	private static LinkedHashMap<String, Integer> vocabs;

	public static void main(String[] args) {
		init("wiki.txt");
		String entry = "to the";
		getWordByScores(entry, 20, -1.0);
	}

	/**
	 * 
	 * @param fileName Non-binary filename
	 */
	private static void init(String fileName) {
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
	
	/**
	 * @param entry to search
	 * @param maxEl Amount of max top results
	 * @param minVal Min dist to consider (between -1 to 1)
	 * 
	 */
	public static void getWordByScores(String entry, int maxEl, double minVal) {
		
		String[] entries = entry.split(" ");

		for (int i = 0; i < entries.length; i++) {// check if entry exists
			Integer index = vocabs.get(entries[i]);
			if (index == null) {
				return;
			}
			System.out.println("Word " + entries[i] + " is at " + index);
		}
		float vec[] = new float[size];
		for (int i = 0; i < size; i++) { vec[i] = 0; }
		for (int i = 0; i < entries.length; i++) {
			int index = vocabs.get(entries[i]);
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
			for (int i = 0; i < entries.length; i++) {
				if (eachEntryMap.getKey().equals(entries[i])) {
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
		for (Entry<String, Double> eachEntryTree : highestScores.entrySet()) {
			System.out.println (eachEntryTree.getKey() + " : " + eachEntryTree.getValue());
		}
	}

}