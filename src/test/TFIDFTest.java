package test;

import static org.junit.Assert.*;

import org.junit.Test;

import tfidf.TFIDF;

public class TFIDFTest {

	@Test
	public void testRunAlgorithm() {
		String docPaths[] = new String[1];
		docPaths[0] = "C:\\Users\\Justin\\Documents\\bbc\\test\\testdocuments";
		int docNums[] = new int[1];
		docNums[0] = 5;
		TFIDF tfidf = new TFIDF(1,"C:\\Users\\Justin\\Documents\\bbc\\test\\testresults",docNums,docPaths);
		
		if(!tfidf.loadAssets()){ //Try to load all the assets
			System.out.println("Load failed, exiting...");
			System.exit(1);
		}
		//tfidf.printLoadDebug();
		tfidf.runAlgorithm();
		//tfidf.printWeightDebug();
		tfidf.compileResults();
		//tfidf.printResults();
		if(!tfidf.writeResults()){
			System.out.println("Writing was unsuccessful");
		}
		System.out.println("Finished!");
		
		//Look for words that have no business being in the top 50
		//Note: inverted if printed
		for(int i = 0; i < 50; i++) {
			//System.out.println(tfidf.getWordsResults().get(0).get(i));
			assertFalse(tfidf.getWordsResults().get(0).get(i).equals("butt"));
			assertFalse(tfidf.getWordsResults().get(0).get(i).equals("off"));
		}
	}

}
