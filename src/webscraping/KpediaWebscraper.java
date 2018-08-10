package webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Singleton class that scrapes the definition of the inputted word from Kpedia.net
 * @author tim
 *
 */
public class KpediaWebscraper {

	private static final KpediaWebscraper instance;
	private static String definition = "";
	private boolean found = false;
	private static Document doc;
	
	static {
		instance = new KpediaWebscraper();
	}
	
	private KpediaWebscraper() {
		//no-arg constructor
	}
	
	public static KpediaWebscraper getInstance() {
        return instance;
    }
	
	//resets all fields that have changed
	private void reset() {
		definition = "";
		found = false;
	}

	/**
	 * Retrieves relevant dictionary entries for parameter word at the Kpedia.jp search page. Does this by
	 * reading HTML of search results page and extracting necessary data according to specific formatting
	 * rules.
	 * @param word to search for
	 * @param url to search at
	 */
	private void searchAndStoreDefinitions(String word) {
		
		try {               
			
			//connect to Kpedia.jp
			doc = Jsoup.connect("http://www.kpedia.jp/s/1/" + word).get();
		    
			//assigns elements to the table on the page (if exists) that will have a 
			//maximum size of 2x50
		    Elements elements = doc.select("table[class=school-course]");
		    
		    //for every row on the table...
		    for (int i = 0; i < elements.get(0).select("td").size(); i += 2) {
		    	
		    	//check if the Korean word (0 and even i, for left column of table) exactly matches the word parameter
		    	//increment by 2 because we want to strictly traverse the left column of the table
		    	if (elements.get(0).select("td").eq(i).get(0).text().substring(0, word.length() + 1).equals(word + "（")) {
		    		
		    		//if it does, add it to the definition String
		    		definition += elements.get(0).select("td").eq(i + 1).get(0).text() + " ";
		    		//and set found to true
		    		found = true;
		    		
		    	} else {
		    	
		    		if (WiktionaryWebscraper.getInstance().getMode() == Mode.OTHER ||
		    				WiktionaryWebscraper.getInstance().getMode() == Mode.VERB) {
		    			
		    			//the first result should compare as being exactly same as the parameter if it is not an adjective
			    		//if it is not, break the loop immediately
		    			break;
		    			
		    		} else {
		    			//due to the behavior of Kpedia.jp's table of randomizing the position of results
		    			//that are adjectives, we go through the entire table until we find a match,
		    			//so we do not break the loop
		    		}
		    		
		    	}
		    	
		    	//replace all commas in the definition String to make it easier to format into an Array
		    	definition = definition.replaceAll("、", " ");
		    	
		    }
		
		} catch (Exception e) {
			
			//if an error of the like comes up, then reset found to false due to possibility of error in the middle of search (where found was set to true)
			found = false;
			
		}
		
	}
	
	/**
	 * Calls on searchAndStoreDefinitions() using the standard Kpedia search URL to get the most
	 * relevant definition results for the parameter word.
	 * @param word to search definitions for
	 * @return String containing the complete definition label
	 */
	public String getDefinition(String word) {
		
		reset();
		
		searchAndStoreDefinitions(word);

		if (found) { //if at least one definition was found
			
			//store the definitions in an array
			String[] definitionsArray = definition.split(" ");
			
			//initialize the definition String with "意味："
			definition = "意味：";
			
			//fill up the definition String with definitionSet's entries
			for (String s : definitionsArray) {
				definition += s + "、";
			}
			
			//chop off the ending comma
			definition = definition.substring(0, definition.length() - 1);
			
		} else { //if found was never changed to true, then definition message becomes "意味は見つかりませんでした。"
			definition = "意味は見つかりませんでした。";
		}
		
		return definition;
		
	}

}
