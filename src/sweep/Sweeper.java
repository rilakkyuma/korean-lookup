package sweep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;

import hangeul.Data;
import webscraping.KpediaWebscraper;
import webscraping.Mode;
import webscraping.WiktionaryWebscraper;

public class Sweeper {
	
	public static final String conjugationsSweepTo = "src/sweep/conjugations.ser";
	public static final String definitionsSweepTo = "src/sweep/definitions.ser";
	
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
		
		exportDefinitionsSerialization();
		exportConjugationsSerialization();
		
	}
	
	public static void throwException() throws DoNotExecuteThisCodeException {
		throw new DoNotExecuteThisCodeException();
	}
	
	
	//export the serialization of the definition memo HashMap
	public static void exportDefinitionsSerialization() {
		try {
	         FileOutputStream filestream = new FileOutputStream(Sweeper.definitionsSweepTo);
	         ObjectOutputStream objectstream = new ObjectOutputStream(filestream);
	         objectstream.writeObject(KpediaWebscraper.getInstance().getDefinitionsMemo());
	         objectstream.close();
	         filestream.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	//export the serialization of the conjugation memo HashMap
	public static void exportConjugationsSerialization() {
		try {
		     FileOutputStream filestream = new FileOutputStream(Sweeper.conjugationsSweepTo);
		     ObjectOutputStream objectstream = new ObjectOutputStream(filestream);
		     objectstream.writeObject(WiktionaryWebscraper.getInstance().getConjugationsMemo());
		     objectstream.close();
		     filestream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws DoNotExecuteThisCodeException {
		
		throwException();
		
		try {
			File file = new File("src/sweep/words");

			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append(" ");
			}
			String[] splitted = stringBuffer.toString().split(" ");
			Sweeper.sweep(splitted);
			fileReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

}
