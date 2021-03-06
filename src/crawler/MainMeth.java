package crawler;

import httpserver.MainWebController;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;

import org.lemurproject.kstem.KrovetzStemmer;

import query.QueryStore;
import query.Word2VecHashConstructor;

public class MainMeth {
	public static void main(String[] args) {
		int threadNo = 30;
		int pageAmount = 1000;
		Iterator<String> argIt = Arrays.asList(args).iterator();
		while (argIt.hasNext()) {
			String argTag = argIt.next();
			if (argTag.equals("-thread") && argIt.hasNext()) {
				threadNo = Integer.valueOf(argIt.next());
			}
			else if (argTag.equals("-pAmount") && argIt.hasNext()) {
				pageAmount = Integer.valueOf(argIt.next());
			}
			else {
				System.out.println("-thread for amount of thread(50 by default)");
				System.out.println("-pAmount for amount of page to download(500 by default)");
				return;
			}
		}
		
		MainSettings.THREAD_AMOUNT = threadNo;
		MainSettings.PAGE_AMOUNT = pageAmount;
		
		KrovetzStemmer kStemmer = new KrovetzStemmer();
		String input = "Singular phone plural 5 different phones. Stemming works!";
		for (String tok : input.split("[^a-zA-Z0-9]+")) {
			System.out.print(kStemmer.stem(tok) + " ");
		}
		
		/*ExampleDocumentHandler docHandle = */
		//new ExampleDocumentHandler();
		QueryStore qStore = QueryStore.getInstance();
		Word2VecHashConstructor.Constructor("", 1000);//Just initialise the word2vec
		
		MainWebController mControl = new MainWebController();
		mControl.setup();
		
		
		

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
		ThreadController tControl = new ThreadController(query, startTime);
		tControl.initialStart(addr);
		
		/*
		MainCrawler mCrawl = new MainCrawler();
		mCrawl.crawlerRunner(addr, query);
		mCrawl.printTop10Pages();
	    long endTime = System.currentTimeMillis();
	    System.out.println("Total execution time: " + (endTime-startTime)/1000 + " sec");*/ 

	}
}
