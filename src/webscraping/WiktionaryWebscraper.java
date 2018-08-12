package webscraping;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Singleton class that scrapes the conjugations of the inputted verbs/adjectives from Wiktionary.org
 * @author tim
 *
 */
public class WiktionaryWebscraper {
	
	private static final int columns = 4;
	private static final int rowsAdjectives = 13; //number of tenses for adjectives
	private static final int rowsVerbs = 17; //number of tenses for verbs, which includes command, etc...
	private static final WiktionaryWebscraper instance;
	private static Map<String, String[][]> conjugations_memo;
	
	private static Mode mode;
	private static Document doc;
	
	private WiktionaryWebscraper() {
		//no-arg constructor
	}
	
	static {
		instance = new WiktionaryWebscraper();
		mode = Mode.INITIALIZED;
		conjugations_memo = new HashMap<>();
	}
	
	//getters
	
	/**
	 * Returns the single instance of this class
	 * @return WiktionaryWebscraper instance
	 */
	public static WiktionaryWebscraper getInstance() {
        return instance;
    }
	
	/**
	 * Returns the memoized conjugations map
	 * @return Map containing currently stored memozied map
	 */
	public Map<String, String[][]> getConjugationsMemo() {
		return conjugations_memo;
	}
	
	/**
	 * Returns the mode currently stored in the static variable
	 * @return Mode containing current mode
	 */
	public Mode getMode() {
		return mode;
	}
	
	public boolean hasConjugations(String word) {
		
		if (conjugations_memo.containsKey(word)) {
			String[][] conjugationtable = conjugations_memo.get(word);
			if (conjugationtable[0][0] == null) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
		
	}
	
	/**
	 * Takes the parameter word and searches the Wiktionary page for the proper part of speech,
	 * and sets the mode variable accordingly
	 * @param word
	 */
	public void setMode(String word) {
		
		//if the word ends in anything but 다 then it is a noun
		if (word.charAt(word.length() - 1) != '다') {
			
			mode = Mode.OTHER;
			
		} else { //if the word does end in 다 then check if it is a verb or adjective
			
			if (conjugations_memo.containsKey(word)) { //if the word has been memoized before...
				
				//check if it is a verb or adjective based on the length of the 2d array
				//because verbs have a different amount of conjugations from adjectives
				if (conjugations_memo.get(word).length == rowsAdjectives) {
					mode = Mode.ADJECTIVE;
				} else if (conjugations_memo.get(word).length == rowsVerbs) {
					mode = Mode.VERB;
				}
				
			} else { // if the word has not yet been memoized, search it up
		
				try {
					//open connection to Wiktionary
					doc = Jsoup.connect("https://en.wiktionary.org/wiki/" + word).get();
			
					//Load the headlines as a list
					Elements headlines = doc.select("span[class=mw-headline]");
					
					//For each of the headlines, check and see if any of the entries matches
					//"Verb" or "Adjective"
					for (Element headline : headlines) {
				
						if (headline.text().equals("Verb")) {
							mode = Mode.VERB;
							break;
						} else if (headline.text().equals("Adjective")) {
							mode = Mode.ADJECTIVE;
							break;
						} else { //if none of them match, then it is something other then a verb/adjective
							mode = Mode.OTHER;
						}
				
					}
		
				} catch (Exception e) {
			
					//if for some reason getting the page fails, we will not want to fill in the conjugation
					//box either way, so set mode to OTHER
					mode = Mode.OTHER;
				
				}
			
			}
			
		}
		
	}
	
	/**
	 * Gets the proper number of conjugations for the inputted word based on whether it is a 
	 * verb or adjective, which is taken from the mode variable which is previously set. If this
	 * method finds a proper set of conjugations, it will also be memoized
	 * @param word Word to get conjugation for
	 * @return 2D array consisting of the conjugations for the verb/adjective
	 */
	public String[][] getConjugations(String word) {
		
		if (mode == Mode.ADJECTIVE) {
			return getAdjectiveConjugations(word);
		} else if (mode == Mode.VERB) {
			return getVerbConjugations(word);
		} else {
			return null;
		}
		
	}
	
	/**
	 * Method that checks Wiktionary page of the inputted verb (if exists) and parses the table HTML
	 * in order to obtain the conjugations and input them into the properly sized 2D array.
	 * @param word Verb to get conjugations for
	 * @return 2D array consisting of the conjugations for the verb
	 */
	public String[][] getVerbConjugations(String word) {
		
		//create a new conjugation table to be filled out
		String[][] conjugationtable = new String[rowsVerbs][columns];
		
		if (conjugations_memo.containsKey(word)) {
			//if the verb conjugations have been memoized before, retrieve them
			conjugationtable = conjugations_memo.get(word);
			
		} else {
		
		try {               
		    
			//select the conjugation table on the Wiktionary page
		    Elements elements = doc.select("table[class=inflection-table]");
		    
		    //from that table, choose specifically the ones that contain conjugations
		    Elements rows = elements.get(0).select("tr");
		    
		    int endIndex = 0;
		    
		    //start going through the table and filling out the 2d array with conjugations
		    //Sentence-final forms
		    for (int i = 2; i < 9; i++) {
		    	for (int j = 0; j < 4; j++) {
		    		try {
		    			if (!rows.get(i).select("td").get(j).text().isEmpty()) {
		    				endIndex = rows.get(i).select("td").get(j).text().indexOf(" ");
		    				conjugationtable[i - 2][j] = rows.get(i).select("td").get(j).text().substring(0, endIndex).replace(",", "");
		    			} else {
		    				conjugationtable[i - 2][j] = "ーーーーーー";
		    			}
		    		} catch (Exception e) {
		    			conjugationtable[i - 2][j] = "ーーーーーー";
		    		}
		    	}
		    }
		    
		    //Connective forms
		    for (int i = 10; i < 15; i++) {
		    	for (int j = 0; j < 4; j++) {
		    		try {
		    			if (!rows.get(i).select("td").get(j).text().isEmpty()) {
		    				endIndex = rows.get(i).select("td").get(j).text().indexOf(" ");
		    				conjugationtable[i - 3][j] = rows.get(i).select("td").get(j).text().substring(0, endIndex).replace(",", "");
		    			} else { //for empty entries
		    				conjugationtable[i - 3][j] = "ーーーーーー";
		    			}
		    		} catch (Exception e) { //for "spaces" that do not have a cell
		    			conjugationtable[i - 3][j] = "ーーーーーー";
		    		}
		    	}
		    }
		    
		    //Noun and determiner forms
		    for (int i = 16; i < 21; i++) {
		    	for (int j = 0; j < 4; j++) {
		    		try {
		    			if (!rows.get(i).select("td").get(j).text().isEmpty()) {
		    				endIndex = rows.get(i).select("td").get(j).text().indexOf(" ");
		    				conjugationtable[i - 4][j] = rows.get(i).select("td").get(j).text().substring(0, endIndex).replace(",", "");
		    			} else {
		    				conjugationtable[i - 4][j] = "ーーーーーー";
		    			}
		    		} catch (Exception e) {
		    			conjugationtable[i - 4][j] = "ーーーーーー";
		    		}
		    	}
		    }
		    
		  //memoize the new conjugation set
		    conjugations_memo.put(word, conjugationtable);
		    
		} catch(Exception e) {  
			System.out.println("\tconjugation table for " + word + " not found...");
	    	conjugations_memo.put(word, conjugationtable);
		} 
		
		}
		
		return conjugationtable;
		
	}
	
	/**
	 * Method that checks Wiktionary page of the inputted adjective (if exists) and parses the table HTML
	 * in order to obtain the conjugations and input them into the properly sized 2D array.
	 * @param word Adjective to get conjugations for
	 * @return 2D array consisting of the conjugations for the adjective
	 */
	public String[][] getAdjectiveConjugations(String word) {
		
		//create a new conjugation table to be filled out
		String[][] conjugationtable = new String[rowsAdjectives][columns];
		
		//if the verb conjugations have been memoized before, retrieve them
		if (conjugations_memo.containsKey(word)) {
			conjugationtable = conjugations_memo.get(word);
		} else {
			
		try {              

			//select the conjugation table on the Wiktionary page
		    Elements elements = doc.select("table[class=inflection-table]");
		    
		    //from that table, choose specifically the ones that contain conjugations
		    Elements rows = elements.get(0).select("tr");
		    
		    //start going through the table and filling out the 2d array with conjugations
		    int endIndex = 0;
		    
		    //Sentence-final forms
		    for (int i = 2; i < 7; i++) {
		    	for (int j = 0; j < 4; j++) {
		    		try {
		    			if (!rows.get(i).select("td").get(j).text().isEmpty()) {
		    				endIndex = rows.get(i).select("td").get(j).text().indexOf(" ");
		    				conjugationtable[i - 2][j] = rows.get(i).select("td").get(j).text().substring(0, endIndex).replace(",", "");
		    			} else {
		    				conjugationtable[i - 2][j] = "ーーーーーー";
		    			}
		    		} catch (Exception e) {
		    			conjugationtable[i - 2][j] = "ーーーーーー";
		    		}
		    	}
		    }
		    
		    //Connective forms
		    for (int i = 8; i < 12; i++) {
		    	for (int j = 0; j < 4; j++) {
		    		try {
		    			if (!rows.get(i).select("td").get(j).text().isEmpty()) {
		    				endIndex = rows.get(i).select("td").get(j).text().indexOf(" ");
		    				conjugationtable[i - 3][j] = rows.get(i).select("td").get(j).text().substring(0, endIndex).replace(",", "");
		    			} else {
		    				conjugationtable[i - 3][j] = "ーーーーーー";
		    			}
		    		} catch (Exception e) {
		    			conjugationtable[i - 3][j] = "ーーーーーー";
		    		}
		    	}
		    }
		    
		    //Noun and determiner forms
		    for (int i = 13; i < 17; i++) {
		    	for (int j = 0; j < 4; j++) {
		    		try {
		    			if (!rows.get(i).select("td").get(j).text().isEmpty()) {
		    				endIndex = rows.get(i).select("td").get(j).text().indexOf(" ");
		    				conjugationtable[i - 4][j] = rows.get(i).select("td").get(j).text().substring(0, endIndex).replace(",", "");
		    			} else {
		    				conjugationtable[i - 4][j] = "ーーーーーー";
		    			}
		    		} catch (Exception e) {
		    			conjugationtable[i - 4][j] = "ーーーーーー";
		    		}
		    	}
		    }
		    
			//memoize the new conjugation set
	    	conjugations_memo.put(word, conjugationtable);
		    
		} catch(Exception e) {  
			System.out.println("\tconjugation table for " + word + " not found...");
	    	conjugations_memo.put(word, conjugationtable);
		} 

		}
		
		return conjugationtable;
		
	}
	
	//export the serialization of the conjugation memo HashMap
		public void exportSerialization() {
			try {
		         FileOutputStream filestream = new FileOutputStream("src/webscraping/conjugations.ser");
		         ObjectOutputStream objectstream = new ObjectOutputStream(filestream);
		         objectstream.writeObject(conjugations_memo);
		         objectstream.close();
		         filestream.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		        }
		}
			
		//read in the serialization of the conjugation memo HashMap, then de-serialize it
		@SuppressWarnings("unchecked")
		public void readInSerialization() {
			try {
			    FileInputStream filestream = new FileInputStream("src/webscraping/conjugations.ser");
			    ObjectInputStream objectstream = new ObjectInputStream(filestream);
			        
			    conjugations_memo = (Map<String, String[][]>) objectstream.readObject();
			        
			    objectstream.close();
			    filestream.close();
			 } catch (IOException | ClassNotFoundException e) {
			    System.out.println("conjugations.ser file not found");
			 }
		}

}
