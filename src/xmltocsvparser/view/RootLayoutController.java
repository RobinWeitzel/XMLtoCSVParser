package xmltocsvparser.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import xmltocsvparser.MainApp;
import xmltocsvparser.model.CustomHeader;
import xmltocsvparser.model.SettingsHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Robin on 03.11.2016.
 */
public class RootLayoutController {

    // Reference to the main application
    private MainApp mainApp;

    @FXML
    private MenuItem readSchema;

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
    private void readSchemaButtonPressed() {
        try {
            FileChooser fileChooser = new FileChooser();

            // Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    "SER file (*.ser)", "*.ser");
            fileChooser.getExtensionFilters().add(extFilter);

            // Show open file dialog
            File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

            if (file != null) {
                FileInputStream fileInputStream = new FileInputStream(file.getAbsoluteFile());
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                ArrayList<CustomHeader> arrayList = new ArrayList<>();
                arrayList = (ArrayList<CustomHeader>) objectInputStream.readObject();
                CustomHeader customHeader = null;

                ObservableList<CustomHeader> headers = mainApp.getHeaders();
                ArrayList<File> paths = mainApp.getFileList();
                ArrayList<Integer> offsetList = mainApp.getOffsetList();

                for (int temp = 0; temp < arrayList.size(); temp++) {
                    customHeader = arrayList.get(temp);
                    headers.add(customHeader);
                }

                if (customHeader != null) {
                    Collections.reverse(paths);
                    Collections.reverse(offsetList);
                    for (int temp = 0; temp < customHeader.getPaths().size(); temp++) {
                        paths.add(new File("Schema " + (temp + 1)));
                        offsetList.add(-1);
                    }
                    Collections.reverse(paths);
                    Collections.reverse(offsetList);
                }

                if (mainApp.getFileList().size() > 0) {
                    mainApp.getOffsetList().remove(0);
                    mainApp.resetFilesProcessedCount();
                    mainApp.matchNextSchema();
                }
                disableMenu(true);

                fileInputStream.close();
                objectInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleNew() {
        ArrayList<File> list = startFileChooser();
        if (list != null) {
            mainApp.setFileList(list);
            mainApp.startSchemaSelection();
        }
    }

    @FXML
    private void handleAdditionalFiles() {
        ArrayList<File> list = startFileChooser();
        if (list != null) {
            mainApp.getFileList().addAll(list);

            if (mainApp.isEndView()) {
                mainApp.setEndView(false);
                mainApp.matchNextSchema();
            }
        }
    }

    @FXML
    private void handleSettings() {
        mainApp.openSettingsWindow();
    }

    @FXML
    private void handleHelp() {
        mainApp.openHelpWindow();
    }

    @FXML
    private void handleTutorial() {
        mainApp.openTutorialWindow();
    }

    @FXML
    private void handleReset() {
        SettingsHandler.resetPrefs();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Information about this programm");
        alert.setContentText("The purpose of this programm is to create one CSV-file from multiple XML-files with different structures.\nIt was written by Robin Weitzel:\nrobin.weitzel.rw@gmail.com.\n\nVersion: 2.0");

        alert.showAndWait();
    }

    public void disableMenu(boolean disable) {
        readSchema.setDisable(disable);
    }

    private ArrayList<File> startFileChooser() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        List<File> list = fileChooser.showOpenMultipleDialog(mainApp.getPrimaryStage());

        return new ArrayList<>(list);
    }
}
