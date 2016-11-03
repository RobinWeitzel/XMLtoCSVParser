package xmltocsvparser.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import xmltocsvparser.MainApp;
import xmltocsvparser.model.CustomHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

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
                ArrayList<CustomHeader> arrayList = (ArrayList<CustomHeader>) objectInputStream.readObject();

                ObservableList<CustomHeader> headers = mainApp.getHeaders();

                for (int temp = 0; temp < arrayList.size(); temp++) {
                    headers.add(new CustomHeader(arrayList.get(temp)));
                }

                mainApp.startRecursiveApplySchema(mainApp.testSchema());
                disableMenu();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableMenu() {
        readSchema.setDisable(true);
    }
}
