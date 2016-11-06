package xmltocsvparser.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
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

    private int position = 0;
    private String[] imageList;

    @FXML
    public void initialize() {
        SettingsHandler.setFirstTime(false);
        String[] imageList = {"1.png", "icon.png", "sprechblase.png", "sprechblase.png", "sprechblase.png", "sprechblase.png"};
        this.imageList = imageList;
        showImage(imageList[position]);
        progressBar.setProgress((((double) position + 1) / (double) imageList.length));
    }

    @FXML
    private void forwardButtonPressed() {
        position = position + 1;
        showImage(imageList[position]);
        back.setDisable(false);
        if (position >= imageList.length - 1) {
            forward.setDisable(true);
        }

        progressBar.setProgress((((double) position + 1) / (double) imageList.length));
    }

    @FXML
    private void backButtonPressed() {
        position = position - 1;
        showImage(imageList[position]);
        forward.setDisable(false);
        if (position == 0) {
            back.setDisable(true);
        }
        progressBar.setProgress((((double) position + 1) / (double) imageList.length));
    }

    private void showImage(String path) {
        //Image image = new Image(MainApp.class.getResource("resources/" + path).toExternalForm());
        //imageView.setImage(image);
    }

}
