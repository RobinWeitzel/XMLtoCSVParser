package xmltocsvparser.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import xmltocsvparser.MainApp;
import xmltocsvparser.model.CSVHandler;
import xmltocsvparser.model.CustomHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by Robin on 03.11.2016.
 */
public class EndController {

    @FXML
    private Button saveXML;
    @FXML
    private Button saveSchema;

    private MainApp mainApp;

    public EndController() {
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
    private void saveXMLButtonPressed() {

        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "CSV file (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) { // Checks if the user chose a filepath where the output should be saved
            CSVHandler csvHandler = new CSVHandler(file.getAbsolutePath(), mainApp.getOffsetList());
            csvHandler.writeHeaderToCSV(mainApp.getHeaders(), mainApp.getFileList()); // Wirtes all headers (containing the headerName and the paths to all selected Nodes) to the CSV-file

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("File has been sucessfully saved");
            alert.showAndWait();
        }
    }

    @FXML
    private void setSaveSchemaButtonPressed() {

        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "SER file (*.ser)", "*.ser");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) { // Checks if the user chose a filepath where the output should be saved
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsoluteFile());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(new ArrayList<CustomHeader>(mainApp.getHeaders()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
