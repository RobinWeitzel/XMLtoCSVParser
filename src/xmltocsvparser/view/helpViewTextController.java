package xmltocsvparser.view;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import xmltocsvparser.model.HelpText;

/**
 * Created by Robin on 06.11.2016.
 */
public class HelpViewTextController {

    @FXML
    WebView webView;

    private WebEngine webEngine;

    private HelpViewController helpViewController;

    @FXML
    public void initialize() {
        webEngine = webView.getEngine();
    }

    public void setHelpViewController(HelpViewController helpViewController) {
        this.helpViewController = helpViewController;
    }

    public void setHelpText(HelpText helpText) {
        webEngine.loadContent(helpText.getHTML());
    }
}
