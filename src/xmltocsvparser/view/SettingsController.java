package xmltocsvparser.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import xmltocsvparser.model.SettingsHandler;

import java.util.ArrayList;

/**
 * Created by Robin on 06.11.2016.
 */
public class SettingsController {

    private static boolean settingShowing = false; // Needed because the settingswindow was rendered twice, this prevents this.
    final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
    @FXML
    private Button seperationCharacterHelper;
    @FXML
    private Button useExcelHelper;
    @FXML
    private Button matchingMethodHelper;
    @FXML
    private Button useWeakPathsHelper;
    @FXML
    private Button threadCountHelper;
    @FXML
    private TextField seperationCharacter;
    @FXML
    private CheckBox useExcel;
    @FXML
    private ChoiceBox<String> matchingMethod;
    @FXML
    private CheckBox useWeakPaths;
    @FXML
    private TextField threadCount;
    @FXML
    private TextField threshold;
    @FXML
    private Button save;
    @FXML
    private Button cancel;
    private Stage stage;

    public static boolean isSettingShowing() {
        return settingShowing;
    }

    public static void setSettingShowing(boolean settingShowing1) {
        settingShowing = settingShowing1;
    }

    @FXML
    public void initialize() {
        seperationCharacter.setText(SettingsHandler.getSeperationCharacter());

        useExcel.setIndeterminate(false); // Checkbox should only have to states, if this is true it has a third
        useExcel.setSelected(SettingsHandler.getUseExcelLayout());

        ArrayList<String> items = new ArrayList<>();
        items.add("manuell");
        items.add("semiautomatic");
        items.add("automatic");
        matchingMethod.setItems(FXCollections.observableList(items));
        matchingMethod.getSelectionModel().select(SettingsHandler.getMatchingMethod());
        matchingMethod.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> selectionChanged());
        selectionChanged();

        useWeakPaths.setIndeterminate(false); // Checkbox should only have to states, if this is true it has a third
        useWeakPaths.setSelected(SettingsHandler.getUseWeakPaths());

        threadCount.setText(Integer.toString(SettingsHandler.getThreadCount()));

        threshold.setText(Double.toString(SettingsHandler.getThreshold()));

        settingShowing = true;
    }

    @FXML
    private void saveButtonPressed() {
        SettingsHandler.setSeperationCharacter(seperationCharacter.getText());
        SettingsHandler.setUseExcelLayout(useExcel.isSelected());
        SettingsHandler.setMatchingMethod(matchingMethod.getValue());
        SettingsHandler.setUseWeakPaths(useWeakPaths.isSelected());
        SettingsHandler.setThreadCount(Integer.parseInt(threadCount.getText()));
        SettingsHandler.setThreshold(Double.parseDouble(threshold.getText()));
        settingShowing = false;
        stage.close();
    }

    @FXML
    private void cancelButtonPressed() {
        settingShowing = false;
        stage.close();
    }

    @FXML
    private void setThreadCountCharacterTyped() {
        try {
            int test = Integer.parseInt(threadCount.getText());
            if (test >= 0) {
                threadCount.pseudoClassStateChanged(errorClass, false);
                save.setDisable(false);
            } else {
                threadCount.pseudoClassStateChanged(errorClass, true);
                save.setDisable(true);
            }
        } catch (NumberFormatException e) {
            threadCount.pseudoClassStateChanged(errorClass, true);
            save.setDisable(true);
        }
    }

    @FXML
    private void setThresholdCharacterTyped() {
        try {
            double test = Double.parseDouble(threshold.getText());
            if (test >= 0 && test <= 1) {
                threshold.pseudoClassStateChanged(errorClass, false);
                save.setDisable(false);
            } else {
                threshold.pseudoClassStateChanged(errorClass, true);
                save.setDisable(true);
            }
        } catch (NumberFormatException e) {
            threshold.pseudoClassStateChanged(errorClass, true);
            save.setDisable(true);
        }
    }

    @FXML
    private void selectionChanged() {
        if (matchingMethod.getSelectionModel().getSelectedItem().equals("semiautomatic")) {
            threshold.setDisable(false);
        } else {
            threshold.setDisable(true);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
