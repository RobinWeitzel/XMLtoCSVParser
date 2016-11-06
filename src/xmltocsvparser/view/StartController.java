package xmltocsvparser.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import xmltocsvparser.MainApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robin on 03.11.2016.
 */
public class StartController {

    @FXML
    private Button tutorial;
    @FXML
    private Button loadXML;

    private MainApp mainApp;

    public StartController() {
    }

    @FXML
    public void initialize() {
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void loadXMLButtonPressed() {
        ArrayList<File> list = startFileChooser();
        if (list != null) {
            mainApp.setFileList(list);
            mainApp.startSchemaSelection();
        }
    }

    @FXML
    public void tutorialButtonPressed() {
        mainApp.openTutorialWindow();
    }

    private ArrayList<File> startFileChooser() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        List<File> list = fileChooser.showOpenMultipleDialog(mainApp.getPrimaryStage());

        if (list != null) {
            return new ArrayList<>(list);
        } else {
            return null;
        }
    }
}
