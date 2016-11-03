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
 * Created by Robin on 20.10.2016.
 */
public class SchemaSelectionWindowController {
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
    private Label leftLabel;
    @FXML
    private Label rightLabel;

    private int idCounter = 0;
    private CustomTreeItem<CustomPath> leftRoot, rightRoot;
    private int offset = -1;
    private MainApp mainApp;


    public SchemaSelectionWindowController() {
    }

    @FXML
    public void initialize() {
        leftRoot = new CustomTreeItem<>();
        this.leftTreeView.setRoot(leftRoot);
        leftTreeView.setShowRoot(false);

        rightRoot = new CustomTreeItem<>();
        this.rightTreeView.setRoot(rightRoot);
        rightTreeView.setShowRoot(false);

        leftTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTreeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    /**
     * Reads in an XML-File and adds all nodes to the left JTree
     */
    public int startAddXML() {

        XMLHandler xmlHandler = new XMLHandler(mainApp.getFileList().get(0).getAbsolutePath());
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
     * Gets called by {@link #startRecursiveAddAllNodesFromXML(XMLHandler, DefaultTreeModel, CustomMutableTreeNode)} after the root has been created
     *
     * @param element          the element to be added to the left JTree
     * @param arrayList        the list containing the path to the parent
     * @param arrayList2       weak path to the parent
     * @param defaultTreeModel TreeModel to which the nodes should be added
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


    private CustomTreeItem<CustomPath> findTreeItem(int id, CustomTreeItem<CustomPath> startPoint) {
        CustomTreeItem<CustomPath> child;
        int childId, maxId = -1, maxIndex = -1;

        if (startPoint.getId() == id) {
            return startPoint;
        } else {
            for (int index = 0; index < startPoint.getChildren().size(); index++) {
                child = (CustomTreeItem) startPoint.getChildren().get(index);
                childId = child.getId();

                if (childId <= id) {
                    if (childId > maxId) {
                        maxId = childId;
                        maxIndex = index;
                    } else {
                        break;
                    }
                }
            }
        }

        if (maxIndex < 0) {
            return null;
        } else {
            // TODO: Check if just using the last child matched in the for-loop does not suffice
            return findTreeItem(id, (CustomTreeItem) startPoint.getChildren().get(maxIndex));
        }
    }

    @FXML
    private void addButtonPressed() {
        ObservableList<TreeItem<CustomPath>> selectedItems = leftTreeView.getSelectionModel().getSelectedItems();

        for (int temp = 0; temp < selectedItems.size(); temp++) {
            addTreeItem((CustomTreeItem) selectedItems.get(temp));
            recursiveAddAllChildNodes((CustomTreeItem) selectedItems.get(temp));
        }
    }

    @FXML
    private void removeButtonPressed() {
        ObservableList<TreeItem<CustomPath>> selectedItems = rightTreeView.getSelectionModel().getSelectedItems();

        for (int temp = 0; temp < selectedItems.size(); temp++) {
            recursiveDeleteNode((CustomTreeItem) selectedItems.get(temp));
        }
    }

    @FXML
    private void confirmButtonPressed() {
        readRightTree(rightRoot, mainApp.getHeaders());
        mainApp.matchNextSchema();
    }


    /**
     * Adds a node and all its parents to the right JTree
     *
     * @param node the node to be added
     */
    private void addTreeItem(CustomTreeItem<CustomPath> treeItem) {
        if (treeItem.getParent() != null) { // Checks if the node has a parent in the left Tree
            CustomTreeItem<CustomPath> parent = findTreeItem(((CustomTreeItem) treeItem.getParent()).getId(), rightRoot);
            CustomTreeItem<CustomPath> child = findTreeItem(treeItem.getId(), rightRoot);

            if (parent == null) { // Checks if the parent in the right JTree exists
                addTreeItem((CustomTreeItem) treeItem.getParent()); // Adds the parent to the right JTree
            }

            parent = findTreeItem(((CustomTreeItem) treeItem.getParent()).getId(), rightRoot);

            if (child == null) { // Checks if the child exists in the right JTree
                child = new CustomTreeItem<CustomPath>(treeItem.getValue(), treeItem.getId());
                parent.getChildren().add(child); // Adds the child-node to the parent in the right tree
            }
        }
    }

    /**
     * Adds al children of a node to the right JTree
     *
     * @param node the node whos children should be added to the right JTree
     */
    private void recursiveAddAllChildNodes(CustomTreeItem<CustomPath> treeItem) {
        CustomTreeItem<CustomPath> foundNode;
        CustomTreeItem<CustomPath> parent;
        int id;
        for (int temp = 0; temp < treeItem.getChildren().size(); temp++) { // Runs through each child of the node
            id = ((CustomTreeItem) treeItem.getChildren().get(temp)).getId();
            parent = findTreeItem(treeItem.getId(), rightRoot); //TODO: Check if parameter (treeItem) can be used as parent
            foundNode = findTreeItem(id, parent);
            if (foundNode == null) { // Makes sure the child does not yet exist
                CustomTreeItem<CustomPath> child = new CustomTreeItem<CustomPath>(treeItem.getChildren().get(temp).getValue(), ((CustomTreeItem) treeItem.getChildren().get(temp)).getId());
                parent.getChildren().add(temp, child); // Adds the child-node to the parent in the right tree
            }
            recursiveAddAllChildNodes((CustomTreeItem<CustomPath>) treeItem.getChildren().get(temp));
        }
    }

    /**
     * Deletes a node and all its children from the right JTree
     * Furthermore removes the parents if the node is the only child (Ensuring no empty parent-nodes exist)
     *
     * @param node the node to be removed
     */
    private void recursiveDeleteNode(CustomTreeItem treeItem) {

        if (treeItem != null) {
            if (treeItem.getParent() != null) { // Makes sure the node exists
                if (treeItem.getParent().getChildren().size() == 1) { // If the parent has no other children (meaning it would be left empty)
                    if (treeItem.getId() == -1) { // If the node is the root
                        rightRoot = new CustomTreeItem<>();
                        this.rightTreeView.setRoot(rightRoot);
                        rightTreeView.setShowRoot(false);
                    } else {
                        recursiveDeleteNode((CustomTreeItem) treeItem.getParent()); // deletes the parent
                    }
                } else {
                    treeItem.getParent().getChildren().remove(treeItem);
                }
            } else {
                rightRoot = new CustomTreeItem<>();
                this.rightTreeView.setRoot(rightRoot);
                rightTreeView.setShowRoot(false);
            }
        }
    }

    private void readRightTree(CustomTreeItem treeItem, ObservableList<CustomHeader> headers) {
        if (treeItem.isLeaf()) {
            if (treeItem.getId() != -1) { // Makes sure the root is not copied
                CustomHeader header = new CustomHeader((CustomPath) treeItem.getValue());
                headers.add(header);
            }
        } else {
            for (int temp = 0; temp < treeItem.getChildren().size(); temp++) {
                readRightTree((CustomTreeItem) treeItem.getChildren().get(temp), headers);
            }
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}