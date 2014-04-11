package crawler;

import java.util.Scanner;

import org.lemurproject.kstem.KrovetzStemmer;

import query.ExampleDocumentHandler;
import query.QueryStore;

public class MainMeth {

	public static void main(String[] args) {
		/*ExampleDocumentHandler docHandle = */
		new ExampleDocumentHandler();
		QueryStore qStore = QueryStore.getInstance();

		KrovetzStemmer kStemmer = new KrovetzStemmer();
		String input = "Today I got a gun, don't shoot me with guns!";
		for (String tok : input.split("[^a-zA-Z]+")) {
			System.out.print(kStemmer.stem(tok) + " ");
		}
		System.out.println("");
		Scanner scan = new Scanner(System.in);
		System.out.println("Please enter the query to be searched");
		String query = kStemmer.stem(scan.nextLine());
		while (qStore.getTermsSize(query) == 0) {
			System.out.println("The query doesn't exist please change your query");
			query = kStemmer.stem(scan.nextLine());
		}
		
		System.out.println("These terms exist in your query:");
		for (String vals : qStore.getTerms(query).keySet()) {
			System.out.println(vals);
		}
		System.out.println("Please enter the root address to be crawled");
		String addr = scan.nextLine();
		scan.close();
		long startTime = System.currentTimeMillis();
		System.out.println("Crawling: " + addr);
		ThreadController tControl = new ThreadController(50, query, 1000, startTime);
		tControl.initialStart(addr);
		
		/*
		MainCrawler mCrawl = new MainCrawler();
		mCrawl.crawlerRunner(addr, query);
		mCrawl.printTop10Pages();
	    long endTime = System.currentTimeMillis();
	    System.out.println("Total execution time: " + (endTime-startTime)/1000 + " sec");*/ 

	}
}
