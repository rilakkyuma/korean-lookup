package sweep;

import hangeul.Data;
import webscraping.KpediaWebscraper;
import webscraping.Mode;
import webscraping.WiktionaryWebscraper;

public class Sweeper {
	
	/**
	 * Method that sweeps Kpedia and Wiktionary, filling in the respective memoized HashMaps
	 * with all possible definitions and conjugations. THIS HAS ALREADY BEEN DONE WITH
	 * RESULTING .SER FILES INCLUDED, SO THERE IS NO NEED TO EVER CALL THIS YOURSELF.
	 * @param allwords
	 */
	protected static void sweep(String[] allwords) {
		
		for (String s : allwords) {
 			
			//check first if the word is written in Korean or Hanja
			if (!KpediaWebscraper.getInstance().hasDefinition(s) && //check if it has already been memoized
					!s.isEmpty() //if not, check if the string is valid
					&& s.charAt(0) >= Data.UNICODE_MIN //check if it is Korean
					&& s.charAt(0) <= Data.UNICODE_MAX) {
				
				WiktionaryWebscraper.getInstance().setMode(s);
				
				System.out.println("getting data for " + s + "...");

				if (WiktionaryWebscraper.getInstance().getMode() == Mode.ADJECTIVE ||
					WiktionaryWebscraper.getInstance().getMode() == Mode.VERB) {

					KpediaWebscraper.getInstance().getDefinition(s);
					WiktionaryWebscraper.getInstance().getConjugations(s);

				} else {
			
					KpediaWebscraper.getInstance().getDefinition(s);
			
				}
			
			}
			
		}
		
		KpediaWebscraper.getInstance().exportSerialization();
		WiktionaryWebscraper.getInstance().exportSerialization();
		
	}

}
