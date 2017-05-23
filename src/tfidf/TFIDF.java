package tfidf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * TF-IDF Tool
 * 
 * This tool implements TF-IDF to rank words within a document set.
 * This is to be coupled with a Deep Belief Network implementation to 
 * classify documents.
 * 
 * @author Justin Rebok
 *
 */
public class TFIDF {

	//All the words that have been scanned
	private List<List<List<String>>> words;
	
	//All the weights of the words that have been scanned
	private List<List<List<Double>>> wordWeights;
	
	//All the weights compressed for each category
	private List<List<Double>> resultsWeights;
	private List<List<String>> resultsWords;
	
	//Information about the folders and documents to scan
	private List<String> path;
	private int pathNum;
	private List<Integer> docPerPath;
	
	//Write path
	private String writePath;
	
	/**
	 * Main Method
	 * @param args
	 */
	public static void main(String[] args) {
		TFIDF tfidf = new TFIDF();
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
	}
	
	/**
	 * Constructor for TFIDF
	 */
	public TFIDF(){
		this.resultsWeights = new ArrayList<List<Double>>();
		this.resultsWords = new ArrayList<List<String>>();
		this.words = new ArrayList<List<List<String>>>();
		this.wordWeights = new ArrayList<List<List<Double>>>();
		this.path = new ArrayList<String>();
		this.docPerPath = new ArrayList<Integer>();
		
		this.pathNum = 5;
		
		//Setup lists for words and weights
		for(int i = 0;i < pathNum; i++){
			this.words.add(new ArrayList<List<String>>());
			this.wordWeights.add(new ArrayList<List<Double>>());
		}
		
		//Set write path
		writePath = "C:\\Users\\Justin\\Documents\\bbc\\results";
		
		//Documents to read for each category
		int docNum0 = 100;
		int docNum1 = 100;
		int docNum2 = 100;
		int docNum3 = 100;
		int docNum4 = 100;
		
		//Add the directories and number of documents to scan
		path.add("C:\\Users\\Justin\\Documents\\bbc\\business");
		docPerPath.add(docNum0);
		path.add("C:\\Users\\Justin\\Documents\\bbc\\entertainment");
		docPerPath.add(docNum1);
		path.add("C:\\Users\\Justin\\Documents\\bbc\\politics");
		docPerPath.add(docNum2);
		path.add("C:\\Users\\Justin\\Documents\\bbc\\sport");
		docPerPath.add(docNum3);
		path.add("C:\\Users\\Justin\\Documents\\bbc\\tech");
		docPerPath.add(docNum4);
		
		//Create all sub lists for the weights
		for(int i = 0; i < docNum0; i++)
			wordWeights.get(0).add(new ArrayList<Double>());
		for(int i = 0; i < docNum1; i++)
			wordWeights.get(1).add(new ArrayList<Double>());
		for(int i = 0; i < docNum2; i++)
			wordWeights.get(2).add(new ArrayList<Double>());
		for(int i = 0; i < docNum3; i++)
			wordWeights.get(3).add(new ArrayList<Double>());
		for(int i = 0; i < docNum4; i++)
			wordWeights.get(4).add(new ArrayList<Double>());
		
		//Setup list for the results
		for(int i = 0; i < pathNum; i++){
			this.resultsWeights.add(new ArrayList<Double>());
			this.resultsWords.add(new ArrayList<String>());
		}
		
		//Do not need to initialize the individual lists for each file
	}

	/**
	 * Writes the results of the TFIDF algorithm to a file
	 * Only writes the 50-most-important-words to some number of files
	 * @return success of writing
	 */
	public boolean writeResults(){
		for(int fileNum = 0; fileNum < this.pathNum; fileNum++){
			try {
				PrintWriter printer = new PrintWriter(writePath + "\\" + Integer.toString(fileNum) + "-Results.txt", "ASCII");
				String top50[] = find50(fileNum);
				for(int i = top50.length-2; i > 0; i--){ //write to file
					printer.println(top50[i]);
				}
				printer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Print the organized 50 weights for debugging
	 * @param category, w, wo
	 */
	private void printFinalWeights(double[] w, String[] wo, int category){
		for(int i = w.length-2; i > -1 ;i--){
			System.out.println("Category: " + Integer.toString(category) + " Word: " + wo[i] + " Weight: " + w[i] + " NUMBER: " + i);
		}

	}
	
	/**
	 * Finds the 50 most important words for some category
	 * @param category
	 * @return
	 */
	private String[] find50(int category){
		String tmpWords[] = new String[51];
		double tmpWeights[] = new double[51];
		
		for(int i = 0; i < resultsWeights.get(category).size();i++){
			int j = 0;
			while(resultsWeights.get(category).get(i)>=tmpWeights[j] && j < tmpWeights.length-1){ //find where it needs to go
				j++;
			}
			int h = 0;
			j--; //take one off to correct it, no idea why this fixes it
			while(h<j){ //move everything less than down a slot
				tmpWeights[h] = tmpWeights[h+1];
				tmpWords[h] = tmpWords[h+1];
				h++;
			}
			if(h > 0){ //if we moved, put new value in
				tmpWords[j] = resultsWords.get(category).get(i);
				tmpWeights[j] = resultsWeights.get(category).get(i);
			}
		}
		//printFinalWeights(tmpWeights,tmpWords,category);
		System.out.println("Found top50 for category: " + Integer.toString(category));
		return tmpWords;
	}
	/**
	 * Print the results, mainly for debugging
	 */
	public void printResults(){
		for(int i = 0; i < pathNum;i++){
			for(int j = 0; j < resultsWords.get(i).size(); j++)
			System.out.println("Category " + Integer.toString(i) + " " + "Word " + resultsWords.get(i).get(j)
					+ " " + "Weight " + Double.toString(resultsWeights.get(i).get(j)));
			
		}
	}
	
	/**
	 * Prints all the contents loaded to the console for debugging
	 */
	public void printLoadDebug(){
		for(int i = 0; i < pathNum;i++){
			for(int j = 0; j < docPerPath.get(i);j++){
				for(String s : words.get(i).get(j)){
					System.out.println("PATH " + Integer.toString(i) + " " + "Document " + Integer.toString(j) + " " + s);
				}
			}
		}
	}
	
	/**
	 * Prints the weight list for debugging
	 */
	public void printWeightDebug(){
		for(int i = 0; i < pathNum;i++){
			for(int j = 0; j < docPerPath.get(i);j++){
				for(double d : wordWeights.get(i).get(j)){
					System.out.println("PATH " + Integer.toString(i) + " " + "Document " + Integer.toString(j) + " " + Double.toString(d));
				}
			}
		}
	}
	
	/**
	 * Loads all the documents in at once.
	 * This approach may not work but we will see
	 * @return success of load
	 */
	public boolean loadAssets(){
		for(int i = 0;i < pathNum;i++){ //Loop through all directories
			int fileNum = 1; //start at file 1
			for(int j = 0;j < docPerPath.get(i);j++){
		
				//Create appropriate string to find the next file
				List<String> f;
				if(fileNum < 10){
					f = readFile(path.get(i) + "\\00" + Integer.toString(fileNum) + ".txt");
				}else if(fileNum < 100){
					f = readFile(path.get(i) +"\\0" + Integer.toString(fileNum) + ".txt");
				}else{
					f = readFile(path.get(i) + "\\" + Integer.toString(fileNum) + ".txt");
				}
		
				if(f==null){ //End algorithm if no file found
					return false; 
				}else{ //Add string list of some file to category i
					words.get(i).add(f);
				}
				fileNum++;
			}
		}
		return true;
	}
	
	/**
	 * Executes tf-idf on the document specified from path
	 * If some term is empty, 0 is assigned
	 */
	public void runAlgorithm(){
		for(int i = 0; i < pathNum;i++){
			for(int j = 0; j < docPerPath.get(i);j++){
				for(String s : words.get(i).get(j)){
					int tf = 0;
					int df = 0;
					double vector = 0;
					if(s != ""){
						tf = doTf(s,i,j); //if not empty
						df = doDf(s,i,docPerPath.get(i));
						vector = tf*Math.log(docPerPath.get(i)/df); //do TFIDF calculation
						wordWeights.get(i).get(j).add(vector); //add vector to the weight list
					}else{ //If the word is empty
						wordWeights.get(i).get(j).add(0.0); //Empty words get 0
					}
					
				}
			}
			System.out.println("Ran TF-IDF on category: " + Integer.toString(i));
		}
	}
	
	/**
	 * Does the DF portion of TFIDF of some string given category and number of documents to scan
	 * @param s
	 * @param category
	 * @param numDocuments
	 * @return df
	 */
	private int doDf(String s, int category, int numDocuments){
		int df = 0;
		for(int i = 0; i < numDocuments; i++){
			for(String tmpS : words.get(category).get(i)){ //check some document i for s
				if(s.equals(tmpS)){
					df++;
					break;
				}
			}
		}
		return df;
	}
	
	/**
	 * Does the TF portion of TFIDF on some string given its category and document numbers
	 * @param s
	 * @return tf
	 */
	private int doTf(String s, int category, int document){
		int tf = 0;
		//Count number of times string appears in document
		for(String tmpS : words.get(category).get(document)){
			if(tmpS.equals(s)) tf++;
		}
		return tf;
	}
	
	/**
	 * Opens a document for the algorithm to read
	 * Reads characters one by one and determines 
	 * where words start and end
	 * @param filepath
	 * @return List<String> words
	 */
	private List<String> readFile(String filepath){
		File f = new File(filepath);
		List<String> fileWords = new ArrayList<String>();
		
		try{ //Try to read the file
			InputStream in = new FileInputStream(f);
			Reader r = new InputStreamReader(in, Charset.forName("ASCII"));
			
			int c;
			String currentWord = "";
			while((c = r.read()) != -1){ //Read the file
				char character = (char) c;
				if(character >= 'A' && character <= 'Z' ||	
						character >= 'a' && character <= 'z' ||
						character == '-' //This will create empty words. Will have to ignore
						){ //Add character to the current word
					currentWord = currentWord + character;
				}else{ //Bad character detected, add current word
					if(currentWord != "") fileWords.add(currentWord); //handle chains of bad characters
					currentWord = "";
				}
			}
		}catch(IOException e){
			System.out.println("Did not find file: " + filepath);
			return null;
		}
		return fileWords;
	}
	
	/**
	 * Condenses the individual weights of each word scanned to give
	 * the 50 most-important-words for the corpus
	 */
	private void compileResults(){
		for(int i = 0; i < pathNum;i++){
			for(int j = 0; j < docPerPath.get(i);j++){
				for(String s : words.get(i).get(j)){
					if(!scanForRepeat(s,i) && s.length() > 2){ //take out two letter words, these are likely junk, may reduce to 1
						resultsWords.get(i).add(s);
						resultsWeights.get(i).add(compressWeight(s,i,docPerPath.get(i)));
					}
				}
			}
			System.out.println("Compressed weights for category: " + Integer.toString(i));
		}
	}
	
	/**
	 * Compress the weights for some word
	 * @param s
	 * @param category
	 * @return weight
	 */
	private double compressWeight(String s, int category, int documentNum){
		double weight = 0;
		for(int i = 0; i < documentNum; i++){
			List<String> tmpDoc = words.get(category).get(i);
			for(int j = 0; j < tmpDoc.size(); j++){
				if(tmpDoc.get(j).equals(s)){
					//update value
					weight = weight + wordWeights.get(category).get(i).get(j);
					//resultsWeights.get(category).set(j,prevWeight + wordWeights.get(category).get(i).get(j));
				}
			}
		}
		
		return weight;
	}
	
	/**
	 * Scans the results for a repeat word
	 * @param s
	 * @param category
	 */
	private boolean scanForRepeat(String s, int category){
		for(String tmpS : resultsWords.get(category)){
			if(tmpS.equals(s)){
				return true;
			}
		}
		return false;
	}
}
