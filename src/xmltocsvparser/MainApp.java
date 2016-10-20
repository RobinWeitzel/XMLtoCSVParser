package xmltocsvparser;/**
 * Created by Robin on 19.10.2016.
 */

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import xmltocsvparser.model.XMLHandler;
import xmltocsvparser.view.LeftTreeController;

public class MainApp extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CSV to XML Parser");
        initRootLayout();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {

            XMLHandler xmlHandler = new XMLHandler("C:\\Users\\Robin\\Documents\\XML Sammlung\\260100023-00-2006-xml.xml");
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/SchemaSelectionWindow.fxml"));
            rootLayout = (AnchorPane) loader.load();
            LeftTreeController leftTreeController = loader.getController();
            System.out.print(leftTreeController.startAddXML(xmlHandler));
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
