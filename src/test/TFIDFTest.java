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
		
		//Words in 4 out of 5 documents, should not appear
		/**
		 * income nut drain sneeze suspend dynamic volcano connect haircut puzzled escape royal agreement rifle fancy rabbits domineering 
eatable summer rainy pin untidy impress best domineering four river discovery enjoy moon stage collar mice gamy dirty cactus 
spotless cushion detect abusive terrific marked satisfying overwrought pizzas cream ugliest horses volatile ball festive 
nonstop mere occur own economic fresh befitting nest cheerful stiff desk cooing unwritten join knock typical rabbits grin 
invention distribution tie light reach cooperative top drain polish purpose bird lean ask punishment swing deep kiss tie suck 
confess scrape crowded bounce petite whole report coordinated faithful little nerve breath
		 */
		
		//Words in the 5th document, should occupy all top 50
		/**
		 * insect wonderful dreary grandmother help grotesque shade women secretive test spotless chickens heat underwear like inject 
wicked flag meaty governor tart event outgoing stage annoy clear courageous decisive low creator small husky opposite license 
grateful serve food tomatoes sheet heap reaction buzz cow somber crayon apparel cherry thinkable worm stretch corn boast obey 
venomous communicate soggy subtract hook obese rainstorm rule preserve adjustment puffy delay snatch macho describe dock clam 
cap jump press roll few moan quaint support cows strip knowledge tomatoes elegant lace drink probable country craven title 
fretful silly floor dogs knock big borrow shrug shame use careful
		 */
		String arr[] = new String[100];
		arr[0] = "insect";
		arr[1] = "wonderful";
		arr[2] = "dreary";
		arr[3] = "grandmother";
		arr[4] = "help";
		arr[5] = "grotesque";
		arr[6] = "shade";
		arr[7] = "women";
		arr[8] = "secretive";
		arr[9] = "test";
		arr[10] = "spotless";
		arr[11] = "chickens";
		arr[12] = "heat";
		arr[13] = "underwear";
		arr[14] = "like";
		arr[15] = "inject";
		arr[16] = "wicked";
		arr[17] = "flag";
		arr[18] = "meaty";
		arr[19] = "governor";
		arr[20] = "tart";
		arr[21] = "event";
		arr[22] = "outgoing";
		arr[23] = "stage";
		arr[24] = "annoy";
		arr[25] = "clear";
		arr[26] = "courageous";
		arr[27] = "decisive";
		arr[28] = "low";
		arr[29] = "creator";
		arr[30] = "small";
		arr[31] = "husky";
		arr[32] = "opposite";
		arr[33] = "license";
		arr[34] = "grateful";
		arr[35] = "serve";
		arr[36] = "food";
		arr[37] = "tomatoes";
		arr[38] = "sheet";
		arr[39] = "heap";
		arr[40] = "reaction";
		arr[41] = "buzz";
		arr[42] = "cow";
		arr[43] = "somber";
		arr[44] = "crayon";
		arr[45] = "apparel";
		arr[46] = "cherry";
		arr[47] = "thinkable";
		arr[48] = "worm";
		arr[49] = "stretch";
		arr[50] = "corn";
		arr[51] = "boast";
		arr[52] = "obey";
		arr[53] = "venomous";
		arr[54] = "communicate";
		arr[55] = "soggy";
		arr[56] = "subtract";
		arr[57] = "hook";
		arr[58] = "obese";
		arr[59] = "rainstorm";
		arr[60] = "rule";
		arr[61] = "preserve";
		arr[62] = "adjustment";
		arr[63] = "puffy";
		arr[64] = "delay";
		arr[65] = "snatch";
		arr[66] = "macho";
		arr[67] = "describe";
		arr[68] = "dock";
		arr[69] = "clam";
		arr[70] = "cap";
		arr[71] = "jump";
		arr[72] = "press";
		arr[73] = "roll";
		arr[74] = "few";
		arr[75] = "moan";
		arr[76] = "quaint";
		arr[77] = "support";
		arr[78] = "cows";
		arr[79] = "strip";
		arr[80] = "knowledge";
		arr[81] = "tomatoes";
		arr[82] = "elegant";
		arr[83] = "lace";
		arr[84] = "drink";
		arr[85] = "probable";
		arr[86] = "country";
		arr[87] = "craven";
		arr[88] = "title";
		arr[89] = "fretful";
		arr[90] = "silly";
		arr[91] = "floor";
		arr[92] = "dogs";
		arr[93] = "knock";
		arr[94] = "big";
		arr[95] = "borrow";
		arr[96] = "shrug";
		arr[97] = "shame";
		arr[98] = "use";
		arr[99] = "careful";
		
		//Look for words that have no business being in the top 50
		//Note: inverted if printed
		for(int i = 0; i < 50; i++) {
			//System.out.println(tfidf.getWordsResults().get(0).get(i));
			assertFalse(tfidf.getWordsResults().get(0).get(i).equals("butt"));
			assertFalse(tfidf.getWordsResults().get(0).get(i).equals("off"));
		}
		
		//Count the number of desired words, should be 50
		int c = 0;
		for(int i = 0;i < 50; i++) {
			for(int j = 0; j < 100; j++) {
				if(tfidf.getWordsResults().get(0).get(i).equals(arr[j]))
					c++;
			}
		}
		//take one off because there is an empty space at the end of the wordsResults array
		assertEquals(50,c-1);
	}

}
