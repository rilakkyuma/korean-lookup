package webscraping;

import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Singleton class that scrapes the definition of the inputted word from Kpedia.net
 * @author tim
 *
 */
public class KpediaWebscraper {

	private static final KpediaWebscraper instance;
	private static final WebClient client = new WebClient();
	
	private static String data = "";
	private static String definition = "";
	private static List<HtmlElement> items;
	private boolean found = false;
	
	static {
		instance = new KpediaWebscraper();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
	}
	
	private KpediaWebscraper() {
		//no-arg constructor
	}
	
	public static KpediaWebscraper getInstance() {
        return instance;
    }
	
	//resets all fields that have changed
	private void reset() {
		data = "";
		definition = "";
		found = false;
	}
	
	public String getDefinition(String word) {
		
		reset();
		
		try {
			
			HtmlPage page = client.getPage("https://www.google.com/search?num=1000&q=" + word + "%20意味%20Kpedia");
			
			items = page.getByXPath("//div[@class='g']");
			
			if (items.isEmpty()) {
				System.out.println("nothing");
			} else {
				
				for (HtmlElement item : items) {
					
					data = item.asText().replaceAll("\n", "");
					
					if (data.indexOf(word) == 0 && data.charAt(word.length()) == 'の' && data.contains("Kpedia")) {
						found = true;
						definition = definition + data.substring(data.indexOf("：") + 1, data.indexOf(" _")) + "、";
					}	
					
				}

				definition = (definition.replaceAll(" ", ""));
				definition = definition.substring(0, definition.length() - 1);
				
				if (found) {
					definition = "意味：" + definition;
				}
				
			}
			
		} catch (Exception e) {
			definition = "意味は見つかりませんでした。";
		}
		
		return definition;
		
	}

}
