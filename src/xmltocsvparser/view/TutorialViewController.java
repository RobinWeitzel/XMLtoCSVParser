package xmltocsvparser.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import xmltocsvparser.MainApp;
import xmltocsvparser.model.SettingsHandler;

/**
 * Created by Robin on 06.11.2016.
 */
public class TutorialViewController {

    @FXML
    private ProgressBar progressBar;
    @FXML
    private ImageView imageView;
    @FXML
    private Button forward;
    @FXML
    private Button back;

    @FXML
    public void initialize() {
        SettingsHandler.setFirstTime(false);
        String string = MainApp.class.getResource("Pictures/1.PNG").toExternalForm();
        Image image = new Image(string);
        imageView.setImage(image);
    }


}
