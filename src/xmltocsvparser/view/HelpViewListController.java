package xmltocsvparser.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import xmltocsvparser.model.HelpText;

/**
 * Created by Robin on 06.11.2016.
 */
public class HelpViewListController {

    @FXML
    ListView<HelpText> listView;

    private HelpViewController helpViewController;

    @FXML
    public void initialize() {
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void setList(ObservableList<HelpText> helpTexts) {
        listView.setItems(helpTexts);
    }

    @FXML
    private void resultSelected() {
        HelpText helpText = listView.getSelectionModel().getSelectedItem();
        if (helpText != null) {
            helpViewController.resultSelected(helpText);
        }
    }

    public void setHelpViewController(HelpViewController helpViewController) {
        this.helpViewController = helpViewController;
    }
}
