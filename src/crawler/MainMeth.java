package crawler;

import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Map.Entry;

import query.ExampleDocumentHandler;

public class MainMeth {

	public static void main(String[] args) {
		ExampleDocumentHandler docHandle = new ExampleDocumentHandler();

		LinkedHashMap<String, LinkedHashMap<String, Integer>> termHash = docHandle
				.getTermHash();
		int totalNum = 0;
		for (Entry<String, LinkedHashMap<String, Integer>> strToTermScorePair : termHash
				.entrySet()) {
			for (Entry<String, Integer> termToScorePair : strToTermScorePair
					.getValue().entrySet()) {
				System.out.println(String.format("%s: %s - %d",
						strToTermScorePair.getKey(), termToScorePair.getKey(),
						termToScorePair.getValue()));
				totalNum++;

			}
		}
		System.out.println(termHash.size() + " total " + totalNum);

		System.out.println("Please enter the root address to be crawled");
		Scanner scan = new Scanner(System.in);
		String addr = scan.nextLine();
		System.out.println("Crawling: " + addr);
		MainCrawler mCrawl = new MainCrawler();
		mCrawl.crawl(addr);
		scan.close();

	}
}
