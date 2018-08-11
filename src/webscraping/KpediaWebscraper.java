package webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that scrapes the definition of the inputted word from Kpedia.net
 * @authors tim, alvin
 * 
 */
public class KpediaWebscraper {

	private static final KpediaWebscraper instance;
	private static StringBuilder definition;
	private static boolean found;
	private static Document doc;
	private static Map<String, String> definitions_memo;
	
	private String word = "";

	static {
		instance = new KpediaWebscraper();
		definition = new StringBuilder();
		found = false;
		definitions_memo = new HashMap<>();
	}

	private KpediaWebscraper() {
		//no-arg constructor
	}

	public static KpediaWebscraper getInstance() {
		return instance;
	}

	//resets all fields that have changed
	private void reset() {
		// clearing definition
		definition.setLength(0);
		word = "";
		found = false;
	}

	// Auxiliary method to replace every "from" to "to" in specified StringBuilder
	private static void replaceAll(StringBuilder builder, String from, String to) {
		int index = builder.indexOf(from);
		while (index != -1) {
			builder.replace(index, index + from.length(), to);
			index += to.length();
			index = builder.indexOf(from, index);
		}
	}
	
	//export the serialization of the definition memo HashMap
	public void exportSerialization() {
		try {
               FileOutputStream filestream = new FileOutputStream("src/webscraping/definitions.ser");
               ObjectOutputStream objectstream = new ObjectOutputStream(filestream);
               objectstream.writeObject(definitions_memo);
               objectstream.close();
               filestream.close();
         } catch (IOException e) {
        	 e.printStackTrace();
         }
	}
	
	//read in the serialization of the definition memo HashMap, then de-serialize it
	@SuppressWarnings("unchecked")
	public void readInSerialization() {
		try {
	        FileInputStream filestream = new FileInputStream("src/webscraping/definitions.ser");
	        ObjectInputStream objectstream = new ObjectInputStream(filestream);
	        
	       definitions_memo = (Map<String, String>) objectstream.readObject();
	        
	        objectstream.close();
	        filestream.close();
	    } catch (IOException | ClassNotFoundException e) {
	    	e.printStackTrace();
	    }
	}

	/**
	 * Retrieves relevant dictionary entries for parameter word at the Kpedia.jp search page. Does this by
	 * reading HTML of search results page and extracting necessary data according to specific formatting
	 * rules.
	 * @param word to search for
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
					definition.append(elements.get(0).select("td").eq(i + 1).get(0).text() + " ");
					//add it to the dictionary
					definitions_memo.put(word, definition.toString());
					//and set found to true
					found = true;

				}

				//replace all commas in the definition String to make it easier to format into an Array
				replaceAll(definition, "、", " ");

			}

		} catch (Exception e) {

			//if an error of the like comes up, then reset found to false due to possibility of error in the middle of search (where found was set to true)
			found = false;
			e.printStackTrace();

		}

	}

	/**
	 * Calls on searchAndStoreDefinitions() using the standard Kpedia search URL to get the most
	 * relevant definition results for the parameter word.
	 * @param word to search definitions for
	 * @return String containing the complete definition label
	 */
	public String getDefinition(String word) {

		// if the search word is the last found word
		// a.k.a. consecutive searches of the same word
		if (this.word.equals(word) && found == true) {
			return definition.toString();
		}
		
		//if the word has been searched before and memoized, format it
		if (definitions_memo.containsKey(word)) {
			//clear the current definition
			definition.setLength(0);
			//append 意味；def1 def2 def3 def4 ... defn
			definition.append("意味：" + definitions_memo.get(word));
			//replace every space with 、
			//making 意味；def1、def2、def3、def4、... defn
			replaceAll(definition, " ", "、");
			//chop off the ending comma
			definition.setLength(definition.length() - 1);
			//return the definition String
			return definition.toString();
		}

		//if this is the first time searching a word, reset the necessary fields
		//and prepare for a new search
		reset();

		searchAndStoreDefinitions(word);

		if (found) { //if at least one definition was found

			//store the definitions in an array
			String[] definitionsArray = definition.toString().split(" ");

			// clear definition
			definition.setLength(0);
			// set definition
			definition.append("意味：");


			//fill up the definition String with definitionSet's entries
			for (String s : definitionsArray) {
				definition.append(s + "、");
			}

			//chop off the ending comma
			definition.setLength(definition.length() - 1);

		} else { //if found was never changed to true, then definition message becomes "意味は見つかりませんでした。"
			definition.setLength(0);
			definition.append("意味は見つかりませんでした。");
		}

		return definition.toString();

	}

}

