package webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
	private static final String serializationLocation = "/definitions.ser";
	
	private String word = "";
	
	private KpediaWebscraper() {
		//no-arg constructor
	}

	static {
		instance = new KpediaWebscraper();
		definition = new StringBuilder();
		found = false;
		definitions_memo = new HashMap<>();
	}
	
	//getters
	
	/**
	 * Returns the single instance of this class
	 * @return KpediaWebscraper instance
	 */
	public static KpediaWebscraper getInstance() {
		return instance;
	}
	
	/**
	 * Returns the memoized conjugations map
	 * @return Map containing currently stored memozied map
	 */
	public Map<String, String> getDefinitionsMemo() {
		return definitions_memo;
	}
	
	/**
	 * Checks if the parameter word has been memoized before, if it is in the memoized HashMap
	 * @param word to check existence of
	 * @return true if the word has been memoized, false if otherwise
	 */
	public boolean hasDefinition(String word) {
		return definitions_memo.containsKey(word);
	}

	//resets all fields that have changed
	private void reset() {
		// clearing definition
		definition.setLength(0);
		word = "";
		found = false;
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

			//if the program ends up here it means that an error happened during retrieving definitions
			//chances are very high that there was no table of definitions found, meaning the parameter word
			//doesn't have an entry on Kpedia.jp
			found = false;
			System.out.println("\tcould not find an entry for " + word);

		}

	}

	/**
	 * Calls on searchAndStoreDefinitions() using the standard Kpedia search URL to get the most
	 * relevant definition results for the parameter word. If this method finds at least one valid
	 * definition, it will be memoized.
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
	

	// Auxiliary method to replace every "from" to "to" in specified StringBuilder
	private static void replaceAll(StringBuilder builder, String from, String to) {
		int index = builder.indexOf(from);
		while (index != -1) {
			builder.replace(index, index + from.length(), to);
			index += to.length();
			index = builder.indexOf(from, index);
		}
	}
		
	//read in the serialization of the definition memo HashMap, then de-serialize it
	@SuppressWarnings("unchecked")
	public void readInSerialization() throws IOException, ClassNotFoundException {
		try {
			InputStream input = getClass().getResourceAsStream(serializationLocation);
			ObjectInputStream objectstream = new ObjectInputStream(input);
			
			definitions_memo = (Map<String, String>) objectstream.readObject();
			
			input.close();
			objectstream.close();
		} catch (Exception e) {
			System.out.println("definitions.ser file not found");
		}
	}

}

