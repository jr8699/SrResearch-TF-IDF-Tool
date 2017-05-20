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
	private List<List<List<Float>>> wordWeights;
	
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
		tfidf.runAlgorithm();
	}
	
	/**
	 * Constructor for TFIDF
	 */
	public TFIDF(){
		this.words = new ArrayList<List<List<String>>>();
		this.wordWeights = new ArrayList<List<List<Float>>>();
		
		this.pathNum = 5;
		
		for(int i = 0;i < pathNum; i++){
			this.words.add(new ArrayList<List<String>>());
			this.wordWeights.add(new ArrayList<List<Float>>());
		}
		
		//Add the directories and number of documents to scan
		path.add("");
		docPerPath.add(0);
		path.add("");
		docPerPath.add(0);
		path.add("");
		docPerPath.add(0);
		path.add("");
		docPerPath.add(0);
		path.add("");
		
		//May not need to do this
		//for(int i = 0;i < docPerPath.get(i); i++){
		//	this.words.get(i).add(new ArrayList<String>());
		//	this.wordWeights.add(new ArrayList<Float>());
		//}
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
					f = readFile(path.get(i) + "/00" + Integer.toString(fileNum));
				}else if(fileNum < 100){
					f = readFile(path.get(i) +"/0" + Integer.toString(fileNum));
				}else{
					f = readFile(path.get(i) + "/" + Integer.toString(fileNum));
				}
		
				if(f==null){ //End algorithm if no file found
					return false; 
				}else{ //Add string list of some file to category i
					words.get(i).add(f);
				}
		
		//STOPPED HERE
			}
		}
		return true;
	}
	
	/**
	 * Executes tf-idf on the document specified from path
	 */
	public void runAlgorithm(){
	}
	
	/**
	 * Opens a document for the algorithm to read
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
				if(character != ' ' && character != '\n'){ //Add character to the current word
					currentWord = currentWord + character;
				}else{ //Space or newline detected, start new word
					fileWords.add(currentWord);
					currentWord = "";
				}
			}
			fileWords.add(currentWord); //Add the last word after done w/ file
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
		
	}
}
