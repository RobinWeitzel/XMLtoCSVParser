package xmltocsvparser.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xmltocsvparser.MainApp;
import xmltocsvparser.model.CustomHeader;
import xmltocsvparser.model.CustomPath;
import xmltocsvparser.model.CustomTreeItem;
import xmltocsvparser.model.XMLHandler;

import java.util.ArrayList;

/**
 * Created by Robin on 03.11.2016.
 */
public class MatchSchemaController {

    @FXML
    private TreeView<CustomPath> leftTreeView;
    @FXML
    private ListView<CustomHeader> rightListView;

    @FXML
    private Button add;
    @FXML
    private Button remove;
    @FXML
    private Button confirm;
    @FXML
    private Button displayTree;
    @FXML
    private Label leftLabel;
    @FXML
    private Label rightLabel;

    private CustomTreeItem<CustomPath> leftRoot;
    private int idCounter = 0;
    private int offset = -1;
    private MainApp mainApp;

    public MatchSchemaController() {
    }

    @FXML
    public void initialize() {
        leftRoot = new CustomTreeItem<>();
        this.leftTreeView.setRoot(leftRoot);
        leftTreeView.setShowRoot(false);
        leftTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Reads in an XML-File and adds all nodes to the left JTree
     */
    public int startAddXML(int position) {

        XMLHandler xmlHandler = new XMLHandler(mainApp.getFileList().get(position).getAbsolutePath());
        Element element;
        NodeList nodeList = xmlHandler.getDocument().getElementsByTagName(xmlHandler.getRootTagName());
        // Adds all children to the root
        if (nodeList != null) { // If a root exists
            int counter = 0;

            for (int temp = 0; temp < nodeList.getLength(); temp++) {
                if (nodeList.item(temp).getNodeType() == Node.ELEMENT_NODE) { // Ensures the selected node is an element (XML contains a lot of none-element nodes which need to be filtered out)
                    ArrayList<Integer> arrayList = new ArrayList<>(0);
                    ArrayList<String> arrayList2 = new ArrayList<>(0);

                    element = (Element) nodeList.item(temp);
                    arrayList.add(counter);
                    counter = counter + 1;
                    arrayList2.add(element.getTagName());
                    leftRoot.getChildren().add(addXML(element, arrayList, arrayList2));
                }
            }
        }
        return offset;
    }

    /**
     * Adds an element and all its children from the XML-file to the lfet JTree
     * Gets called by {@link #(XMLHandler, , )} after the root has been created
     *
     * @param element    the element to be added to the left JTree
     * @param arrayList  the list containing the path to the parent
     * @param arrayList2 weak path to the parent
     * @param to         which the nodes should be added
     * @return returns the node after it has been added to the JTree
     */
    private CustomTreeItem<CustomPath> addXML(Element element, ArrayList<Integer> arrayList, ArrayList<String> arrayList2) { // Ensures the selected node is an element (XML contains a lot of none-element nodes which need to be filtered out)
        CustomPath customPath = new CustomPath(arrayList, arrayList2, element.getTagName());
        CustomTreeItem customTreeItem = new CustomTreeItem(customPath, idCounter++);
        boolean isChild = true;

        NodeList nodeList = element.getChildNodes();
        int counter = 0;

        for (int temp = 0; temp < nodeList.getLength(); temp++) {
            if (nodeList.item(temp).getNodeType() == Node.ELEMENT_NODE) {
                if (offset == -1) {
                    offset = temp;
                }
                ArrayList<Integer> arrayListCopy = new ArrayList<>(arrayList);
                ArrayList<String> arrayList2Copy = new ArrayList<>(arrayList2);
                Element newElement = (Element) nodeList.item(temp);
                arrayListCopy.add(counter);
                counter = counter + 1;
                arrayList2Copy.add(newElement.getTagName());
                customTreeItem.getChildren().add(addXML(newElement, arrayListCopy, arrayList2Copy));
                isChild = false;
            }
        }
        if (isChild) {
            customTreeItem.setTextContent(element.getTextContent());
        }
        return customTreeItem;
    }

    public void loadListView() {
        mainApp.getHeaders().add(new CustomHeader());
        rightListView.setItems(mainApp.getHeaders());
    }

    @FXML
    private void addButtonPressed() {
        ObservableList<TreeItem<CustomPath>> selectedItemsLeft = leftTreeView.getSelectionModel().getSelectedItems();
        ObservableList<CustomHeader> selectedItemsRight = rightListView.getSelectionModel().getSelectedItems();
        CustomHeader customHeader;

        for (int temp = 0; temp < Math.min(selectedItemsLeft.size(), selectedItemsRight.size()); temp++) {
            customHeader = selectedItemsRight.get(temp);

            if (customHeader.isTrueHeader()) {
                customHeader.setTempPath(selectedItemsLeft.get(temp).getValue());
            } else { // Header is not true means the addElement-Header was selected.
                mainApp.getHeaders().remove(customHeader);
                ArrayList<CustomPath> paths;

                for (; temp < selectedItemsLeft.size(); temp++) {
                    paths = new ArrayList<>();
                    getChildCollection((CustomTreeItem) selectedItemsLeft.get(temp), paths);

                    for (int temp2 = 0; temp2 < paths.size(); temp2++) {
                        mainApp.getHeaders().add(new CustomHeader(paths.get(temp2), false));
                    }
                }
                mainApp.getHeaders().add(new CustomHeader());
            }
        }

        rightListView.refresh();
    }

    private void getChildCollection(CustomTreeItem treeItem, ArrayList<CustomPath> paths) {
        if (treeItem.isLeaf()) {
            if (treeItem.getId() != -1) { // Makes sure the root is not copied
                paths.add((CustomPath) treeItem.getValue());
            }
        } else {
            for (int temp = 0; temp < treeItem.getChildren().size(); temp++) {
                getChildCollection((CustomTreeItem) treeItem.getChildren().get(temp), paths);
            }
        }
    }

    @FXML
    private void removeButtonPressed() {
        ObservableList<CustomHeader> selectedItemsRight = rightListView.getSelectionModel().getSelectedItems();
        CustomHeader customHeader;

        for (int temp = 0; temp < selectedItemsRight.size(); temp++) {
            customHeader = selectedItemsRight.get(temp);
            if (customHeader != null) {
                if (customHeader.isTrueHeader()) {
                    if (customHeader.isWriteProtected()) {
                        customHeader.setTempPath(null);
                    } else {
                        mainApp.getHeaders().remove(customHeader);
                    }
                }
            }
        }
        rightListView.refresh();
    }

    @FXML
    public void confirmButtonPressed() {
        readRightList();
        mainApp.matchNextSchema();
    }

    @FXML
    private void displayTreeButtonPressed() {
        if (mainApp.getBestSchema() != -1) {
            mainApp.displayTree();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Headers can only be displayed as tree if a schema was selected");
            alert.showAndWait();
        }
    }

    private void readRightList() {
        ObservableList<CustomHeader> headers = mainApp.getHeaders();
        CustomHeader customHeader;

        for (int temp = 0; temp < headers.size(); temp++) {
            customHeader = headers.get(temp);
            if (customHeader.isTrueHeader()) {
                if (customHeader.isWriteProtected()) {
                    if (!customHeader.convertTempPath()) { // False if no temppath exists
                        customHeader.addBlank();
                    }
                } else {
                    customHeader.matchHeaderToSize(mainApp.getFilesProcessedCount() + 1);
                    headers.add(customHeader);
                }
            } else {
                headers.remove(customHeader);
            }

        }
    }
}
