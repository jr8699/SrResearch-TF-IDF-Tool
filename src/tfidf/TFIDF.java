package tfidf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
		tfidf.printResults();
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
		
		//Add the directories and number of documents to scan
		path.add("C:\\Users\\Justin\\Documents\\bbc\\business");
		docPerPath.add(2);
		path.add("C:\\Users\\Justin\\Documents\\bbc\\entertainment");
		docPerPath.add(2);
		path.add("C:\\Users\\Justin\\Documents\\bbc\\politics");
		docPerPath.add(2);
		path.add("C:\\Users\\Justin\\Documents\\bbc\\sport");
		docPerPath.add(2);
		path.add("C:\\Users\\Justin\\Documents\\bbc\\tech");
		docPerPath.add(2);
		
		//NOTE: UPDATE THIS IF ABOVE TERMS ARE UPDATED
		//Create all sub lists for the weights
		for(int i = 0; i < 2; i++)
			wordWeights.get(0).add(new ArrayList<Double>());
		for(int i = 0; i < 2; i++)
			wordWeights.get(1).add(new ArrayList<Double>());
		for(int i = 0; i < 2; i++)
			wordWeights.get(2).add(new ArrayList<Double>());
		for(int i = 0; i < 2; i++)
			wordWeights.get(3).add(new ArrayList<Double>());
		for(int i = 0; i < 2; i++)
			wordWeights.get(4).add(new ArrayList<Double>());
		
		//Setup list for the results
		for(int i = 0; i < pathNum; i++){
			this.resultsWeights.add(new ArrayList<Double>());
			this.resultsWords.add(new ArrayList<String>());
		}
		
		//Do not need to initialize the individual lists for each file
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
	 * @return
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
		}
	}
	
	/**
	 * Does the DF portion of TFIDF of some string given category and number of documents to scan
	 * @param s
	 * @param category
	 * @param numDocuments
	 * @return
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
	 * @return
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
	 * @return
	 */
	private List<String> readFile(String filepath){
		File f = new File(filepath);
		List<String> fileWords = new ArrayList<String>();
		
		try{ //Try to read the file
			InputStream in = new FileInputStream(f);
			Reader r = new InputStreamReader(in, Charset.defaultCharset());
			
			int c;
			String currentWord = "";
			while((c = r.read()) != -1){ //Read the file
				char character = (char) c;
				if(character != ' ' 			//This will create empty words. Will have to ignore when
						&& character != '\n' 	//compiling the results
						&& character != '.' 
						&& character != ',' 
						&& character != ';' 
						&& character != '"'
						&& character != '('
						&& character != ')'){ //Add character to the current word
					currentWord = currentWord + character;
				}else{ //Space or newline detected, start new word
					fileWords.add(currentWord);
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
					if(!scanForRepeat(s,i)){
						resultsWords.get(i).add(s);
						resultsWeights.get(i).add(compressWeight(s,i,docPerPath.get(i)));
					}
				}
			}
		}
	}
	
	/**
	 * Compress the weights for some word
	 * @param s
	 * @param category
	 * @return
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
