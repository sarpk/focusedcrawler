package crawler;

import java.util.Scanner;

import query.ExampleDocumentHandler;
import query.QueryStore;

public class MainMeth {

	public static void main(String[] args) {
		/*ExampleDocumentHandler docHandle = */
		new ExampleDocumentHandler();
		QueryStore qStore = QueryStore.getInstance();

		
		Scanner scan = new Scanner(System.in);
		System.out.println("Please enter the query to be searched");
		String query = scan.nextLine();
		while (qStore.getTermsSize(query) == 0) {
			System.out.println("The query doesn't exist please change your query");
			query = scan.nextLine();
		}
		
		System.out.println("These terms exist in your query:");
		for (String vals : qStore.getTerms(query).keySet()) {
			System.out.println(vals);
		}
		System.out.println("Please enter the root address to be crawled");
		String addr = scan.nextLine();
		System.out.println("Crawling: " + addr);
		MainCrawler mCrawl = new MainCrawler();
		mCrawl.crawl(addr, query);
		scan.close();
		mCrawl.printTop10Pages();

	}
}
