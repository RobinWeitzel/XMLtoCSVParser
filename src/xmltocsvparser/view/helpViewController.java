package xmltocsvparser.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import xmltocsvparser.MainApp;
import xmltocsvparser.model.HelpText;

/**
 * Created by Robin on 06.11.2016.
 */
public class HelpViewController {

    @FXML
    TextField searchText;
    @FXML
    Button searchButton;
    @FXML
    BorderPane borderPane;

    private ObservableList<HelpText> helpTexts;

    @FXML
    public void initialize() {
        helpTexts = FXCollections.observableArrayList();
        setHelpTexts();
        startSearch(null);

        searchText.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                startSearch(searchText.getText().toLowerCase());
            }
        });
    }

    @FXML
    private void searchButtonPressed() {
        startSearch(searchText.getText().toLowerCase());
    }

    private void setHelpTexts() {
        String name = "Schema";
        String[] keywords = {"schema", "load", "export", "schemas"};
        String text = "The Schema contains information on how XML-files of different formats can be combined into one CSV file.<br>A schema can be exported and imported via the menu. Only one schema can be applied to XML-files at the same time.*<br><br>*This is because one schema represents the columns in the CSV-file. Since all XML-files should be saved in one file, only one schema can be loaded at all times.";
        HelpText helpText = new HelpText(name, keywords, text);
        helpTexts.add(helpText);

        name = "XML";
        String[] keywords2 = {"xml", "file", "load", "input"};
        text = "XML (Extensive Markup Language) is a markup language that defines a set of rules for encoding documents.<br><br>Inputfiles for this programm have to be XML-files. These files are gathered into one CSV-file.";
        helpText = new HelpText(name, keywords2, text);
        helpTexts.add(helpText);

        name = "CSV";
        String[] keywords3 = {"csv", "file", "save", "export"};
        text = "CSV (Comma Seperated Values) is a file format storing tabular data in plain text. Each line of the file is a data record. Each record consists of one or more fields, separated by commas.<br><br>The output file of this program is one CSV-file. Each line in this file stands for one XML-file, each column for on node in the XML-file.\nThe character seperating each row can be set via the settings menu.";
        helpText = new HelpText(name, keywords3, text);
        helpTexts.add(helpText);
    }

    public void startSearch(String input) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/helpViewList.fxml"));
            AnchorPane start = loader.load();
            borderPane.setCenter(start);

            HelpViewListController controller = loader.getController();
            controller.setHelpViewController(this);

            if (input == null || input.isEmpty()) { // If nothing was entered in the searchbar
                controller.setList(helpTexts);
            } else { // If something was entered
                ObservableList<HelpText> searchResult = FXCollections.observableArrayList();
                for (int temp = 0; temp < helpTexts.size(); temp++) {
                    if (helpTexts.get(temp).checkKeyword(input)) {
                        searchResult.add(helpTexts.get(temp));
                    }
                }
                controller.setList(searchResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resultSelected(HelpText helpText) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/helpViewText.fxml"));
            AnchorPane start = loader.load();
            borderPane.setCenter(start);

            HelpViewTextController controller = loader.getController();
            controller.setHelpViewController(this);
            controller.setHelpText(helpText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
