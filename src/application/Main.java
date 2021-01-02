package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import webscraping.KpediaWebscraper;
import webscraping.WiktionaryWebscraper;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	
	@Override
    public void start(Stage primaryStage) throws Exception {
		
		KpediaWebscraper.getInstance().readInSerialization();
		WiktionaryWebscraper.getInstance().readInSerialization();

        // just load fxml file and display it in the stage:

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainUI.fxml"));
        loader.setController(new FXMLController());
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        primaryStage.getIcons().add(new Image("assets/icon.png"));
        primaryStage.setTitle("Korean Lookup");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
