package xmltocsvparser.view;

import javafx.collections.FXCollections;
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
 * Created by Robin on 04.11.2016.
 */
public class MatchSchemaTreeController {

    @FXML
    private TreeView<CustomPath> leftTreeView;
    @FXML
    private TreeView<CustomPath> rightTreeView;
    @FXML
    private Button add;
    @FXML
    private Button remove;
    @FXML
    private Button confirm;
    @FXML
    private Button showList;
    @FXML
    private Label leftLabel;
    @FXML
    private Label rightLabel;

    private int idCounter = 0;
    private CustomTreeItem<CustomPath> leftRoot, rightRoot;
    private int offset = -1;
    private MainApp mainApp;
    private int schemaUsed;


    public MatchSchemaTreeController() {
    }

    @FXML
    public void initialize() {
        leftRoot = new CustomTreeItem<>();
        this.leftTreeView.setRoot(leftRoot);
        leftTreeView.setShowRoot(false);

        rightRoot = new CustomTreeItem<>();
        this.rightTreeView.setRoot(rightRoot);
        rightTreeView.setShowRoot(false);

        leftTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        rightTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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
    private void addButtonPressed() {

        TreeItem<CustomPath> treeItemRight = rightTreeView.getSelectionModel().getSelectedItem();
        TreeItem<CustomPath> treeItemLeft = leftTreeView.getSelectionModel().getSelectedItem();

        CustomHeader head;
        ArrayList<CustomHeader> customHeaderCollection = new ArrayList<>(0);
        ObservableList<CustomHeader> headers = mainApp.getHeaders();
        int[] strongPath = treeItemRight.getValue().getNodes();
        int[] headStrongPath;
        boolean matches;

        // Gets all Headers that are children of the selected node
        for (int temp = 0; temp < headers.size(); temp++) {
            head = headers.get(temp);
            headStrongPath = head.getPath(schemaUsed).getNodes();
            matches = true;
            if (strongPath.length <= headStrongPath.length) {
                for (int temp2 = 0; temp2 < strongPath.length; temp2++) {
                    if (strongPath[temp2] != headStrongPath[temp2]) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    customHeaderCollection.add(head);
                }
            }
        }

        CustomPath path = treeItemLeft.getValue();
        mainApp.recursiveApplySchema(FXCollections.observableList(customHeaderCollection), path, schemaUsed, treeItemRight.getValue().getNodes().length);
    }

    @FXML
    private void removeButtonPressed() {

    }

    @FXML
    private void showListButtonPressed() {
        mainApp.displayList();
    }

    @FXML
    private void confirmButtonPressed() {
        mainApp.displayListAndContinue();
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

    public void startCreateTree(int index) {

        ArrayList<ArrayList<CustomHeader>> headerContainer = new ArrayList<>();
        ArrayList<Integer> pathCollection = new ArrayList<>();
        ArrayList<String> weakPathCollection = new ArrayList<>();

        ArrayList<Integer> pathCollection2 = new ArrayList<>();
        ArrayList<String> weakPathCollection2 = new ArrayList<>();

        schemaUsed = index;
        CustomPath customPath;
        int node;
        ObservableList<CustomHeader> headers = mainApp.getHeaders();
        int position = 0;

        for (int temp = 0; temp < headers.size(); temp++) {
            if (headers.get(temp).isTrueHeader()) {
                customPath = headers.get(temp).getPath(index);
                if (position < customPath.getNodes().length) {
                    node = customPath.getNodes()[position];
                    if (!pathCollection.contains(node)) {
                        pathCollection.add(node);
                        weakPathCollection.add(customPath.getWeakNodes()[position]);
                        ArrayList<CustomHeader> header = new ArrayList<>();
                        header.add(headers.get(temp));
                        headerContainer.add(header);
                    } else {
                        headerContainer.get(pathCollection.indexOf(node)).add(headers.get(temp));
                    }
                }
            }
        }

        for (int temp = 0; temp < headerContainer.size(); temp++) {
            CustomPath path = new CustomPath(pathCollection.get(temp), weakPathCollection.get(temp));
            CustomTreeItem<CustomPath> newTreeItem = createTree(FXCollections.observableList(headerContainer.get(temp)), index, position + 1, path);
            if (newTreeItem != null) {
                rightRoot.getChildren().add(newTreeItem);
            }
        }
    }


    public CustomTreeItem<CustomPath> createTree(ObservableList<CustomHeader> headers, int index, int position, CustomPath parentPath) {

        ArrayList<ArrayList<CustomHeader>> headerContainer = new ArrayList<>();
        ArrayList<Integer> pathCollection = new ArrayList<>();
        ArrayList<String> weakPathCollection = new ArrayList<>();
        CustomPath customPath;
        CustomTreeItem<CustomPath> customTreeItem = new CustomTreeItem<>(parentPath, 1);
        int node;

        for (int temp = 0; temp < headers.size(); temp++) {
            if (headers.get(temp).isTrueHeader()) {
                customPath = headers.get(temp).getPath(index);
                if (position < customPath.getNodes().length) {
                    node = customPath.getNodes()[position];
                    if (!pathCollection.contains(node)) {
                        pathCollection.add(node);
                        weakPathCollection.add(customPath.getWeakNodes()[position]);
                        ArrayList<CustomHeader> header = new ArrayList<>();
                        header.add(headers.get(temp));
                        headerContainer.add(header);
                    } else {
                        headerContainer.get(pathCollection.indexOf(node)).add(headers.get(temp));
                    }
                }
            }
        }

        for (int temp = 0; temp < headerContainer.size(); temp++) {
            CustomPath path = new CustomPath(parentPath, pathCollection.get(temp), weakPathCollection.get(temp));
            CustomTreeItem<CustomPath> newTreeItem = createTree(FXCollections.observableList(headerContainer.get(temp)), index, position + 1, path);
            if (newTreeItem != null) {
                customTreeItem.getChildren().add(newTreeItem);
            }
        }
        return customTreeItem;
    }

    public void setLabel(String text) {
        rightLabel.setText(text);
    }
}
