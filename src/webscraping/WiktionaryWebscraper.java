package webscraping;

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
	private static final int rowsAdjectives = 13;
	private static final int rowsVerbs = 17;
	private static final WiktionaryWebscraper instance;
	
	private static Mode mode;
	private static Document doc;
	
	static {
		instance = new WiktionaryWebscraper();
		mode = Mode.INITIALIZED;
	}
	
	public Mode getMode() {
		return mode;
	}
	
	private WiktionaryWebscraper() {
		//no-arg constructor
	}
	
	public static WiktionaryWebscraper getInstance() {
        return instance;
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
	
	/**
	 * Gets the proper number of conjugations for the inputted word based on whether it is a 
	 * verb or adjective, which is taken from the mode variable which is previously set.
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
		
		String[][] conjugationtable = new String[rowsVerbs][columns];
		
		try {               
		    
		    Elements elements = doc.select("table[class=inflection-table]");
		    
		    Elements rows = elements.get(0).select("tr");
		    
		    int endIndex = 0;
		    
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
		    			System.out.println(i + ", " + j);
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
		    
		} catch(Exception e) {  
		    e.printStackTrace();   
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
		
		String[][] conjugationtable = new String[rowsAdjectives][columns];
		
		try {              
		    
		    Elements elements = doc.select("table[class=inflection-table]");
		    
		    Elements rows = elements.get(0).select("tr");
		    
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
		    			System.out.println(i + ", " + j);
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
		    
		} catch(Exception e) {  
		    e.printStackTrace();   
		} 
		
		return conjugationtable;
		
	}

}
