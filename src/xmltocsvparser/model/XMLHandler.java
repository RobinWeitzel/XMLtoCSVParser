package xmltocsvparser.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

/**
 * Handles reading and processing XML-files
 *
 * @author Robin Weitzel
 */
public class XMLHandler {

    private File inputFile;
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;
    private int offset;

    /**
     *
     * @param pathToXML the full path poiting to an XML-file
     */
    public XMLHandler(String pathToXML) {
        try {
            inputFile = new File (pathToXML);
            this.factory = DocumentBuilderFactory.newInstance();
            this.builder = factory.newDocumentBuilder();
            this.document = this.builder.parse(this.inputFile);
            this.document.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param pathToXML the full path poiting to an XML-file
     * @param offset the offset of all nodes being read
     */
    public XMLHandler(String pathToXML, int offset) {
        try {
            inputFile = new File (pathToXML);
            this.factory = DocumentBuilderFactory.newInstance();
            this.builder = factory.newDocumentBuilder();
            this.document = this.builder.parse(this.inputFile);
            this.document.getDocumentElement().normalize();
            this.offset = offset;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To retrieve the Document
     *
     * @return the document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * To retrieve the name of the root
     *
     * @return the name of the Document-root
     */
    public String getRootTagName() {
        Element element = document.getDocumentElement();
        return element.getTagName();
    }

    /**
     * Starts searching the XML-file for a node
     *
     * @param tagName the name of the root
     */
    public void startRecursiveGetChildNodes(String tagName){
        int temp;
        NodeList nodeList = document.getElementsByTagName(tagName);

        if (nodeList.getLength() < 1) {
            return;
        } else {
            for (temp = 0; temp < nodeList.getLength(); temp++) {
                Node node = nodeList.item(temp);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    recursiveGetChildNodes(node);
                }
            }
        }
    }

    /**
     * Seaches the XML-file for a node
     *
     * @param node the node to search for
     * @return true if a node was found
     */
    public boolean recursiveGetChildNodes(Node node) {
        int temp;

        if (node.hasChildNodes()) {
            NodeList nodeList = node.getChildNodes();
            for (temp = 0; temp < nodeList.getLength(); temp++) {
                Node newnode = nodeList.item(temp);
                if (recursiveGetChildNodes(newnode)) {
                    if (nodeList.getLength() == 1) {
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    /**
     * Finds a node by using strong paths starting from document-root
     *
     * @param path the path to the node to search for
     * @return a node if one wass found, otherwise null
     */
    public Node getNodeByPath (CustomPath path) {

        if (!path.isTruePath()) { // Checks path is not a filler
            return null;
        } else { // If path is not a filler
            NodeList nodeList = document.getDocumentElement().getChildNodes();
            Node node = null;
            int pathNode;

            if (path.getNodes()[0] == 0 && path.getTagName() == document.getDocumentElement().getTagName() && path.getNodes().length == 1) { // Meaning the node points to the document-root
                return document.getDocumentElement();
            }

            if (path == null) {
                return node;
            }

            for (int temp = 1; temp < path.getNodes().length; temp++) {
                pathNode = path.getNodes()[temp] + path.getNodes()[temp] * offset + offset;
                if (pathNode < nodeList.getLength()) {
                    node = nodeList.item(pathNode);
                    if (node != null) {
                        nodeList = node.getChildNodes();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            if (node != null && node.getNodeName().equals(path.getTagName())) { // If a node was found at the path and its tag-name matches (Chance very high this is the correct node)
                return node;
            } else {
                return null;
            }
        }
    }

    /**
     * Finds a node from a variable starting point using strong paths
     *
     * @param path the path to the node we are looking for
     * @param startPoint the point from where to start
     * @param parent the node before the startingPoint
     * @return a Node if one was found, otherwise null
     */
    public Node getNodeByPath (CustomPath path, int startPoint, Node parent) {

        if (!path.isTruePath()) { // Checks if path is a filler
            return null;
        } else {
            NodeList nodeList = parent.getChildNodes();
            Node node = null;
            int pathNode;

            if (path == null) {
                return node;
            }

            for (int temp = startPoint; temp < path.getNodes().length; temp++) {
                pathNode = path.getNodes()[temp] + path.getNodes()[temp] * offset + offset;
                if (pathNode < nodeList.getLength()) {
                    node = nodeList.item(pathNode);
                    if (node.hasChildNodes()) {
                        nodeList = node.getChildNodes();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
            if (node != null && node.getNodeName().equals(path.getTagName())) { // If a node was found and the tag-name matches
                return node;
            } else {
                return null;
            }
        }
    }

    /**
     * Gets a node using weak paths starting from the document-root
     *
     * @param path The path of the node we are looking for
     * @return Path to the found node or null if none was found
     */
    public CustomPath startRecursiveGetNodeByWeakPath (CustomPath path) {
        if (path == null) {
            return null;
        }
        if (!path.isTruePath()) {
            return null;
        } else {
            ArrayList<Integer> newPath = new ArrayList<>(0);
            newPath.add(0);
            return recursiveGetNodeByWeakPath(path, 1, document.getDocumentElement(),newPath);
        }
    }

    /**
     * Finds a node using weak paths starting from a variable starting point
     *
     * @param path the path to the node we are looking for
     * @param startPoint the point from where to start
     * @param parent the node before the startingPoint
     * @param newPath A Collection of paths that need to be seached
     * @return Path to the found node or null if none was found
     */
    private CustomPath recursiveGetNodeByWeakPath (CustomPath path, int startPoint, Node parent, ArrayList<Integer> newPath) {
        if (!path.isTruePath()) {
            return null;
        } else {
            Node node = getNodeByPath(path, startPoint, parent);
            CustomPath returnPath = null;
            if (node != null) { // meaning the searched node matches this node
                for (;startPoint < path.getNodes().length; startPoint++) { // Adds the path we used to get here to the path of the found node (so that strong paths can be used in the future to retrieve this node)
                    newPath.add(path.getNodes()[startPoint]);
                }
                return new CustomPath(newPath,path.getWeakNodes(), path.getTagName());
            } else { // If node does not match the one we are looking for
                if (startPoint < path.getWeakNodes().length) { // Ensures there are still children to search
                    String nodeName = path.getWeakNodes()[startPoint];
                    NodeList nodeList = parent.getChildNodes();
                    ArrayList<Integer> copyNewPath;

                    for (int temp = 0; temp < nodeList.getLength(); temp++) {
                        node = nodeList.item(temp);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            if (element.getTagName().equals(nodeName)) {
                                copyNewPath = new ArrayList<>(newPath);
                                copyNewPath.add(temp);
                                returnPath = recursiveGetNodeByWeakPath(path, startPoint + 1, node, copyNewPath);

                                if (returnPath != null) {
                                    return returnPath;
                                }
                            }
                        }
                    }
                }
                return null;
            }
        }
    }

    /**
     * Used in the parallel testing of paths
     * Finds one child if it matches the strong path
     * Otherwise returns a collection of children that match the weak path
     *
     * @see xmltocsv.ParallelTestNewSchema
     * @param path the path to the node we are looking for
     * @param searchNumber the Position in the XML-file where the child should be found
     * @param searchName the tag-Name of the child we are looking for
     * @return a collection of children matching the search-criteria
     */
    public ArrayList<CustomPath> parallelSearchHelper (CustomPath path, int searchNumber, String searchName) {
        Node node = getNodeByPath(path);
        int newSearchNumber = searchNumber + searchNumber * offset + offset; // Accounts for the offset some XML-files have
        ArrayList<CustomPath> pathCollection = new ArrayList<>(0);

        if (node == null || newSearchNumber >= node.getChildNodes().getLength()) { // If the node cant contain the child
            return pathCollection;
        } else { // Checks if the child can be found using strong paths
            NodeList nodeList = node.getChildNodes();
            if (node.hasChildNodes()) {
                Node child = nodeList.item(newSearchNumber);
                if (child != null) {
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) child;
                        if (element.getTagName().equals(searchName)) {
                            pathCollection.add(new CustomPath(path, searchNumber, searchName));
                            return pathCollection;
                        }
                    }
                }

                // Only get here if the child could not be found using strong paths
                for (int temp = 0; temp < nodeList.getLength(); temp++) {
                    if (temp == searchNumber) {
                        continue;
                    } else {
                        child = nodeList.item(temp);
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) child;
                            if (element.getTagName().equals(searchName)) {
                                int newtemp = (temp - offset) / (offset + 1);
                                pathCollection.add(new CustomPath(path, newtemp, searchName));
                            }
                        }
                    }
                }
            }
            return pathCollection;
        }
    }

    /**
     * To set the offset
     *
     * @param offset a number between [0, infinity[. This number shows how many "empty" nodes are found by the XMLReader before the next "real" node is found (if this mber is not fixed the sheet needs to be reformatted)
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }
}

