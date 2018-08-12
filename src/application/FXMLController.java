package application;

import hangeul.Data;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import webscraping.KpediaWebscraper;
import webscraping.Mode;
import webscraping.WiktionaryWebscraper;

public class FXMLController {
	
	private static String text = "";
	private static String substring = "";
	private static String newstring = "";
	private static int startIndex = 0; //initialize length to 0
	private static int endIndex = 0; //initialize index to 0
	private static int currentLevel = 0; //initialize to 0, keeps track of current politeness level, 1 being most casual and 4 being most polite
	private static String[][] conjugations;
	
	@FXML
	private TextField inputTango;
	
	@FXML
	private RadioButton radio1, radio2, radio3, radio4;
	
	@FXML
	private Button search;
	
	@FXML
	private Label status;
	
	@FXML
	private AnchorPane conjugationBox, ADJsubBox, VERBsubBox, conjugationTableNotFound;
	
	@FXML //labels where conjugated Korean adjectives will go
	private Label ADJ1, ADJ2, ADJ3, ADJ4, ADJ5, ADJ6, ADJ7, ADJ8, ADJ9, ADJ10, ADJ11, ADJ12, ADJ13;
	
	@FXML //labels where conjugated Korean verbs will go
	private Label VERB1, VERB2, VERB3, VERB4, VERB5, VERB6, VERB7, VERB8, VERB9, VERB10, VERB11, VERB12, VERB13, VERB14, VERB15, VERB16, VERB17;
	
	/***
	 * Changes all four radio buttons to the parameter boolean.
	 * @param bool - boolean to set all 4 radiosDisabled to. Used when switching between adj/verb and noun terms.
	 * If the boolean is false, the radios will be enabled, and disabled if otherwise.
	 */
	private void setRadiosDisabled(boolean bool) {
		radio1.setDisable(bool);
		radio2.setDisable(bool);
		radio3.setDisable(bool);
		radio4.setDisable(bool);
	}
	
	/***
	 * Given the 2D array, this method populates the labels on the GUI with the proper conjugations
	 */
	private void setConjugations() {
		
		if (WiktionaryWebscraper.getInstance().getMode() == Mode.ADJECTIVE) {
			ADJ1.setText(conjugations[0][currentLevel]);
			ADJ2.setText(conjugations[1][currentLevel]);
			ADJ3.setText(conjugations[2][currentLevel]);
			ADJ4.setText(conjugations[3][currentLevel]);
			ADJ5.setText(conjugations[4][currentLevel]);
			ADJ6.setText(conjugations[5][currentLevel]);
			ADJ7.setText(conjugations[6][currentLevel]);
			ADJ8.setText(conjugations[7][currentLevel]);
			ADJ9.setText(conjugations[8][currentLevel]);
			ADJ10.setText(conjugations[9][currentLevel]);
			ADJ11.setText(conjugations[10][currentLevel]);
			ADJ12.setText(conjugations[11][currentLevel]);
			ADJ13.setText(conjugations[12][currentLevel]);
		} else if (WiktionaryWebscraper.getInstance().getMode() == Mode.VERB) {
			VERB1.setText(conjugations[0][currentLevel]);
			VERB2.setText(conjugations[1][currentLevel]);
			VERB3.setText(conjugations[2][currentLevel]);
			VERB4.setText(conjugations[3][currentLevel]);
			VERB5.setText(conjugations[4][currentLevel]);
			VERB6.setText(conjugations[5][currentLevel]);
			VERB7.setText(conjugations[6][currentLevel]);
			VERB8.setText(conjugations[7][currentLevel]);
			VERB9.setText(conjugations[8][currentLevel]);
			VERB10.setText(conjugations[9][currentLevel]);
			VERB11.setText(conjugations[10][currentLevel]);
			VERB12.setText(conjugations[11][currentLevel]);
			VERB13.setText(conjugations[12][currentLevel]);
			VERB14.setText(conjugations[13][currentLevel]);
			VERB15.setText(conjugations[14][currentLevel]);
			VERB16.setText(conjugations[15][currentLevel]);
			VERB17.setText(conjugations[16][currentLevel]);
		}
		
	}
	
	/***
	 * Grabs the type of the inputted word which will determine the size of the 2D array to re-assign.
	 * If the inputted word is a noun, nothing special will be printed but the meaning. If the word is
	 * an adjective/verb, this method grabs the HTML of the Wiktionary page for the verb (if found) and
	 * parses the conjugations into the 2D array, which then will populate the verb/adjective boxes in the GUI.
	 * This method also enables radios if they were not already before, and causes the proper verb/adjective
	 * boxes to be visible to the user. If the word was a noun, the politeness level boxes will be disabled.
	 */
	@FXML
	private void inputDefinitionsAndConjugations() {
		
		String input = inputTango.getText();
		
		WiktionaryWebscraper.getInstance().setMode(input);
		
		if (WiktionaryWebscraper.getInstance().getMode() == Mode.ADJECTIVE) {
			
			status.setText(KpediaWebscraper.getInstance().getDefinition(input));
			conjugations = WiktionaryWebscraper.getInstance().getConjugations(input);
			
			//if there are valid definitions for the input...
			if (WiktionaryWebscraper.getInstance().hasConjugations(input)) {
				
				//show the conjugations
				setConjugations();
				setRadiosDisabled(false);
				conjugationTableNotFound.setVisible(false);
				VERBsubBox.setVisible(false);
				ADJsubBox.setVisible(true);
				conjugationBox.setVisible(true);
				
			} else {
				
				//otherwise, show only the "Conjugations not found" box
				
				conjugationBox.setVisible(false);
				setRadiosDisabled(true);
				ADJsubBox.setVisible(false);
				VERBsubBox.setVisible(false);
				conjugationTableNotFound.setVisible(true);
			
			}
			
		} else if (WiktionaryWebscraper.getInstance().getMode() == Mode.VERB) {
			
			status.setText(KpediaWebscraper.getInstance().getDefinition(input));
			conjugations = WiktionaryWebscraper.getInstance().getConjugations(input);
			
			//if there are valid definitions for the input...
			if (WiktionaryWebscraper.getInstance().hasConjugations(input)) {
				
				//show the conjugations
				setConjugations();
				setRadiosDisabled(false);
				conjugationTableNotFound.setVisible(false);
				ADJsubBox.setVisible(false);
				VERBsubBox.setVisible(true);
				conjugationBox.setVisible(true);
				
			} else {
				
				//otherwise, show only the "Conjugations not found" box
			
				conjugationBox.setVisible(false);
				setRadiosDisabled(true);
				ADJsubBox.setVisible(false);
				VERBsubBox.setVisible(false);
				conjugationTableNotFound.setVisible(true);
			
			}
			
		} else {
			
			status.setText(KpediaWebscraper.getInstance().getDefinition(input));

			setRadiosDisabled(true);
			ADJsubBox.setVisible(false);
			VERBsubBox.setVisible(false);
			conjugationBox.setVisible(false);
			conjugationTableNotFound.setVisible(true);
			
		}
		
	}
	
	/***
	 * Calls inputDefinitionsAndConjugations() to inject values into their proper fields.
	 * @param event - when the button is clicked on
	 */
	@FXML
	private void searchButtonClickListener(MouseEvent event) {
		
		inputDefinitionsAndConjugations();
		
	}
	
	/***
	 * 
	 * @param event - When SPACE button is pressed, performs replacement of Roman text is possible.
	 * When ENTER button is pressed, acts as if the Search button was clicked.
	 */
	@FXML
	private void inputKeyListener(KeyEvent event){
		
		if (event.getCode() == KeyCode.ENTER) {
			
			inputDefinitionsAndConjugations();
			
		}
		
	    if (event.getCode() == KeyCode.SPACE) {
	    	
	    	  text = inputTango.getText();
	    	  text = text.trim();
	    	  
	    	  newstring = text; //if the field is cleared, pressing SPACE will not fill up the field with what was not there
	    	  
	    	  endIndex = inputTango.getCaretPosition() - 1;
	    	  startIndex = endIndex - 7;
	    	  
	    	  //if the startIndex is less than text's length, increment it until the startIndex is at LEAST 0
	    	  while (startIndex < 0) {
	    		  startIndex++;
	    	  }
	    	  
	    	  if (text.length() > 0) { //if the textfield has at least one character
	    	  
	    		  //check from [end - 7] to [end] for a match, if a match is not found, increment the startIndex
	    		  //7 is used because that is the longest length of any romanized Korean character
	    		  while (startIndex < endIndex && 
	    				  !Data.roma_han.containsKey(text.substring(startIndex, endIndex))) {
	    			  startIndex++;
	    		  }
	    		
	    	  	substring = text.substring(startIndex, endIndex);
	    	  	
	    	  	if (Data.roma_han.containsKey(substring)) {
	    	  		//ensures that everything before and after the transformation is untouched
	    	  		newstring = (text.substring(0, startIndex) + Data.roma_han.get(substring) + text.substring(endIndex, text.length()));
	    	  		newstring = newstring.replaceAll("\\s", ""); //destroy any white space
	    	  	}
	    	  
	    	  }
	    	  
	    	  inputTango.setText(newstring);
	    	  inputTango.positionCaret(Integer.MAX_VALUE); //positions the caret at the very end
	    	  
	     }
	}
	
	//All four methods below ensure that at least one button is ticked at all times, so
	//there cannot be an instant where nothing is ticked
	
	@FXML
	private void click1(MouseEvent event) {
		radio1.setSelected(true);
	}
	
	@FXML
	private void click2(MouseEvent event) {
		radio2.setSelected(true);
	}
	
	@FXML
	private void click3(MouseEvent event) {
		radio3.setSelected(true);
	}
	
	@FXML
	private void click4(MouseEvent event) {
		radio4.setSelected(true);
	}
	
	//All four methods below disable the proper radio buttons to ensure that ONLY one is checked at a time.
	
	@FXML
	private void deselect1(MouseEvent event) {
		currentLevel = 1;
		setConjugations();
		radio2.setSelected(false);
		radio3.setSelected(false);
		radio4.setSelected(false);
	}
	
	@FXML
	private void deselect2(MouseEvent event) {
		currentLevel = 2;
		setConjugations();
		radio1.setSelected(false);
		radio3.setSelected(false);
		radio4.setSelected(false);
	}
	
	@FXML
	private void deselect3(MouseEvent event) {
		currentLevel = 0;
		setConjugations();
		radio1.setSelected(false);
		radio2.setSelected(false);
		radio4.setSelected(false);
	}
	
	@FXML
	private void deselect4(MouseEvent event) {
		currentLevel = 3;
		setConjugations();
		radio1.setSelected(false);
		radio2.setSelected(false);
		radio3.setSelected(false);
	}
	
	//When the application starts up, ensures that the first radio button is checked by default.
	
	@FXML
	public void initialize() {
		radio1.setSelected(true);
		currentLevel = 1;
	}

}