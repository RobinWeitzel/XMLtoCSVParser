package xmltocsvparser;/**
 * Created by Robin on 19.10.2016.
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.w3c.dom.Node;
import xmltocsvparser.model.CustomHeader;
import xmltocsvparser.model.CustomPath;
import xmltocsvparser.model.ParallelTestNewSchema;
import xmltocsvparser.model.XMLHandler;
import xmltocsvparser.view.*;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private List<File> fileList;
    private ArrayList<Integer> offsetList;
    private ObservableList<CustomHeader> headers;
    private int threadCount = 8; // How many threads should be used
    private int filesProcessedCount; // how many files have been processed
    private boolean useWeakPaths = true; // If weak paths should be used
    private XMLHandler xmlHandler;
    private RootLayoutController rootLayoutController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CSV to XML Parser");
        offsetList = new ArrayList<>();
        headers = FXCollections.observableArrayList();
        initRootLayout();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            rootLayoutController = loader.getController();
            rootLayoutController.setMainApp(this);
            primaryStage.show();

            loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/start.fxml"));
            AnchorPane start = loader.load();

            rootLayout.setCenter(start);

            StartController startController = loader.getController();
            startController.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startSchemaSelection() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SchemaSelectionWindow.fxml"));
            AnchorPane start = loader.load();

            rootLayout.setCenter(start);

            SchemaSelectionWindowController controller = loader.getController();
            controller.setMainApp(this);
            offsetList.add(controller.startAddXML());
            filesProcessedCount = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void matchNextSchema() {
        rootLayoutController.disableMenu();
        try {
            if (filesProcessedCount + 1 < fileList.size()) { // If there are more files to be processed
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApp.class.getResource("view/matchSchema.fxml"));
                AnchorPane start = loader.load();

                rootLayout.setCenter(start);

                MatchSchemaController controller = loader.getController();
                controller.setMainApp(this);
                offsetList.add(controller.startAddXML(++filesProcessedCount));
                xmlHandler = new XMLHandler(fileList.get(filesProcessedCount).getAbsolutePath(), offsetList.get(filesProcessedCount));
                int bestSchema = testSchema();
                if (bestSchema != -1) {
                    startRecursiveApplySchema(bestSchema);
                }
                controller.loadListView();
            } else {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApp.class.getResource("view/end.fxml"));
                AnchorPane start = loader.load();

                rootLayout.setCenter(start);

                EndController controller = loader.getController();
                controller.setMainApp(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public int testSchema() {

        String string;
        ArrayList<String> arrayList = new ArrayList<>(0);
        double schemaTest;
        double maxSchemaTest = 0;
        int bestTemp = 0;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount); // Uses up to 10 threads at the same time
        List<Future<Double>> list = new ArrayList<Future<Double>>(); // Used to get the value once a thread finishes

        // Applys the paths saved in the header to the new XML-File to find the best match
        for (int temp = 0; temp < filesProcessedCount; temp++) {
            Future<Double> future = executor.submit(new ParallelTestNewSchema(headers, fileList.get(filesProcessedCount).getAbsolutePath(), temp, useWeakPaths, true, offsetList.get(filesProcessedCount)));
            list.add(future);

        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        for (int temp = 0; temp < list.size(); temp++) {
            try {
                string = fileList.get(temp).getAbsolutePath();
                // Tests the schema and displays a percentage of how well the schema fits
                schemaTest = list.get(temp).get(); // Gets the value of a thread, waits if value has not yet been calculated
                string = string.substring(string.lastIndexOf("\\") + 1, string.length());
                string = string + " (" + decimalFormat.format(schemaTest) + ")";
                arrayList.add(string);

                // Checks if the new schema fits better than all the old ones
                if (schemaTest > maxSchemaTest) {
                    maxSchemaTest = schemaTest;
                    bestTemp = temp;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Dialog asking user to choose schema
        ChoiceDialog<String> dialog = new ChoiceDialog<>(arrayList.get(bestTemp), arrayList);
        dialog.setTitle("Available schema");
        dialog.setHeaderText("Please choose the schema you wish to apply");
        dialog.setContentText("Schema:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return arrayList.lastIndexOf(result.get());
        }

        return -1;
    }

    /**
     * Applies the schema of an XML-file to a new one.
     * Updates  headers to contain new XML-file as temp-paths
     * Uses weak paths, starts {@link #(ArrayList, CustomPath, int, int)}
     *
     * @param index the schema to be used (corresponds to the files in order they were read in)
     */
    public void startRecursiveApplySchema(int index) {

        if (headers.size() == 0) { // If no schema exists
            return;
        } else {
            CustomPath newpath = headers.get(0).getPath(index);
            if (newpath.isTruePath()) { // Makes sure the path is not just a filler
                int[] array = {newpath.getNodes()[0]};
                String[] array2 = {newpath.getWeakNodes()[0]};
                CustomPath path = new CustomPath(array, array2, array2[0]);
                recursiveApplySchema(headers, path, index, path.getNodes().length);
            }
        }
    }

    /**
     * Recursively applies a schema to a new file
     * <p>
     * Idea:    Split tree into buckets according to their strong-path beginning
     * Check if this node exists using weak paths, if not drop the bucket
     *
     * @param headers the list of headers inside the bucket coming in
     * @param path    the path to the parent
     * @param index   the schema to be used (corresponds to the files in order they were read in)
     */
    private void recursiveApplySchema(ObservableList<CustomHeader> headers, CustomPath path, int index, int startPoint) {

        ArrayList<ArrayList<CustomHeader>> headerContainer = new ArrayList<>(0);
        ArrayList<Integer> positionalMapping = new ArrayList<>(0);
        ArrayList<CustomPath> pathCollection;
        CustomPath newPath, newPath2;
        Node node;
        int pathLength = startPoint, position = 0, foundPosition;
        int[] nodes;

        for (int temp = 0; temp < headers.size(); temp++) {
            newPath2 = headers.get(temp).getPath(index);
            if (newPath2.isTruePath()) { // Makes sure the path is not just a filler
                nodes = newPath2.getNodes();
                if (pathLength >= nodes.length - 1) { //If the path is as long as the path of the header (meaning we have reached the node that headers.get(temp) points to)
                    newPath = new CustomPath(path, nodes[nodes.length - 1], headers.get(temp).getPath(index).getWeakNodes()[nodes.length - 1]);
                    node = xmlHandler.getNodeByPath(newPath);
                    if (node != null) {
                        headers.get(temp).setTempPath(newPath);
                    }
                } else { // If we have not reached the end of the path
                    if (!positionalMapping.contains(nodes[pathLength])) { // If the bucket for this node does not exist creates it
                        headerContainer.add(new ArrayList<CustomHeader>(0));
                        positionalMapping.add(nodes[pathLength]);
                        headerContainer.get(position).add(headers.get(temp));
                        position = position + 1;
                    } else { // If the bucket already exists only adds the node
                        foundPosition = positionalMapping.indexOf(nodes[pathLength]);
                        headerContainer.get(foundPosition).add(headers.get(temp));
                    }
                }
            }
        }

        for (int temp = 0; temp < positionalMapping.size(); temp++) { // Goes through all Buckets
            pathCollection = xmlHandler.parallelSearchHelper(path, positionalMapping.get(temp), headerContainer.get(temp).get(0).getPath(index).getWeakNodes()[pathLength]);
            for (int temp2 = 0; temp2 < pathCollection.size(); temp2++) { // If a child was found
                recursiveApplySchema(FXCollections.observableList(headerContainer.get(temp)), pathCollection.get(temp2), index, startPoint + 1);
            }
        }
    }

    public ArrayList<Integer> getOffsetList() {
        return offsetList;
    }

    public void setOffsetList(ArrayList<Integer> offsetList) {
        this.offsetList = offsetList;
    }

    public ObservableList<CustomHeader> getHeaders() {
        return headers;
    }

    public void setHeaders(ObservableList<CustomHeader> headers) {
        this.headers = headers;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getFilesProcessedCount() {
        return filesProcessedCount;
    }

    public void setFilesProcessedCount(int filesProcessedCount) {
        this.filesProcessedCount = filesProcessedCount;
    }

    public boolean isUseWeakPaths() {
        return useWeakPaths;
    }

    public void setUseWeakPaths(boolean useWeakPaths) {
        this.useWeakPaths = useWeakPaths;
    }
}
