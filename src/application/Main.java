package application;
	
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import webscraping.KpediaWebscraper;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	
	@Override
    public void start(Stage primaryStage) throws Exception {
		
		KpediaWebscraper.getInstance().readInSerialization();

        // just load fxml file and display it in the stage:

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainUI.fxml"));
        loader.setController(new FXMLController());
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        //when the program is closed, export the definitions serialization to a .ser file
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent t) {
            	KpediaWebscraper.getInstance().exportSerialization();
            }
        });
        
        primaryStage.getIcons().add(new Image("assets/icon.png"));
        primaryStage.setTitle("Korean Lookup");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
