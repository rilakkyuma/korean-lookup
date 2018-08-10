package webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
	private static String word;
	private static boolean found;
	private static Document doc;
	private static Map<String, String> memo;

	static {
		instance = new KpediaWebscraper();
		definition = new StringBuilder();
		word = "";
		found = false;
		memo = new HashMap<>();
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
					memo.put(word, definition.toString());
					//and set found to true
					found = true;

				}

				//replace all commas in the definition String to make it easier to format into an Array
				replaceAll(definition, "、", " ");

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

		// last found word
		if (this.word.equals(word) && found == true)
			return definition.toString();
		if (memo.containsKey(word))
			return memo.get(word);

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

