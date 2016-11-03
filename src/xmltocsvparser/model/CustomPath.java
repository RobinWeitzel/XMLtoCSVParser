package xmltocsvparser.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The path to a node in an XML-file
 * A node can be identified (with reasonable surety) by a strong path and its tag-name
 * A strong path is a collection of numbers which match to which child of a parent node to take
 * E.g: 0:3:2::Name - means, take documentroot (0), fro root take third child (3), than its second child (2). If this child has the tag-name <Name> it is the node we are looking for
 *
 * A weak path uses the tag-names of all nodes traversed to reach the child instead of numbers. This can cause trouble because some parents may have children with the same name.
 *
 * For performance, only using strong paths is recommended. However, these only work well if the XML-documents have the _exact_ same structure.
 * Otherwise a combination of strong and weak paths is recommended
 *
 * @author Robin Weitzel
 */
public class CustomPath implements Serializable {
    private int[] nodes; // The exact path to a node, an Array containing all the turns needed to get there
    private String[] weakNodes; // A weak path using names (which might be used more than once in an XML-file)
    private String tagName;
    private boolean isTruePath = true;

    /**
     * This constructure is only used to create "false" Paths.
     * A false path is used if an XML-file contains nodes that were not matched to a tag-name.
     * However, to keep the structure of headers intact (one Custompath for each file), false Paths are added
     *
     * These nodes will be represented in the CSV-file by the text "missing"
     *
     * @param isTruePath
     */
    public CustomPath(boolean isTruePath) {
        this.isTruePath = isTruePath;
        this.tagName = "missing";
        weakNodes = new String[0];
    }

    /**
     * Standard constructor
     *
     * @param nodes a array containing the set of directions to reach the node
     * @param weekNodes a array containing the weak path
     * @param tagName the tag-name to identify the node
     */
    public CustomPath(int[] nodes, String[] weekNodes, String tagName) {
        this.nodes = new int[nodes.length];
        this.weakNodes = new String[weekNodes.length];

        for (int temp = 0; temp < nodes.length; temp++) {
            this.nodes[temp] = nodes[temp];
            this.weakNodes[temp] = weekNodes[temp];
        }
        this.tagName = tagName;
    }

    /**
     * Standard constructor
     *
     * @param nodes a ArrayList containing the set of directions to reach the node
     * @param weekNodes a ArrayList containing the weak path
     * @param tagName the tag-name to identify the node
     */
    public CustomPath(ArrayList<Integer> nodes, String[] weekNodes, String tagName) {
        this.nodes = new int[nodes.size()];
        this.weakNodes = new String[weekNodes.length];

        for(int temp = 0; temp < nodes.size(); temp++) {
            this.nodes[temp] = nodes.get(temp);
            this.weakNodes[temp] = weekNodes[temp];
        }
        this.tagName = tagName;
    }

    /**
     * Standard constructor
     *
     * @param nodes a ArrayList containing the set of directions to reach the node
     * @param weekNodes a ArrayList containing the weak path
     * @param tagName the tag-name to identify the node
     */
    public CustomPath(ArrayList<Integer> nodes, ArrayList<String> weekNodes, String tagName) {
        this.nodes = new int[nodes.size()];
        this.weakNodes = new String[weekNodes.size()];

        for(int temp = 0; temp < nodes.size(); temp++) {
            this.nodes[temp] = nodes.get(temp);
            this.weakNodes[temp] = weekNodes.get(temp);
        }
        this.tagName = tagName;
    }

    /**
     * Used to construct a new CustomPath from an old one and an added set of directions
     *
     * @param path the old path
     * @param node the added direction added to the end of the new path
     * @param weakNode the added weakNode to be added to the weak path. Also represents the tag-name
     */
    public CustomPath(CustomPath path, int node, String weakNode) {
        int temp;
        this.nodes = new int[path.getNodes().length + 1];
        this.weakNodes = new String[path.getWeakNodes().length + 1];

        // adds the path and weakpath from the old Custompath to the new one
        for (temp = 0; temp < path.getNodes().length; temp++) {
            this.nodes[temp] = path.getNodes()[temp];
            this.weakNodes[temp] = path.getWeakNodes()[temp];
        }

        // adds the added set of directions
        this.nodes[temp] = node;
        this.weakNodes[temp] = weakNode;
        this.tagName = weakNode;
    }

    /**
     * To check if a path is a tur path.
     * For explanation se {@link #CustomPath(boolean)}
     *
     * @return true if it is a true path, otherwise false
     */
    public boolean isTruePath() {
        return isTruePath;
    }

    /**
     * Returns the tag-name
     *
     * @return the tag-name
     */
    public String getTagName () {
        return tagName;
    }

    /**
     * Returns the array containing the strong path
     *
     * @return the strong path
     */
    public int[] getNodes () {
        return this.nodes;
    }

    /**
     * Returns the array containing the weak path
     *
     * @return weak path
     */
    public String[] getWeakNodes () {
        return weakNodes;
    }

    /**
     * Custom toString Method to insure Paths are displayed correctly in the JList
     *
     * @Override
     * @return
     */
    public String toString () {
        return tagName;
    }

    /**
     * Returns a String off the weak path seperated by a ":"
     */
    public String weakPathToString () {
        StringBuilder stringBuilder = new StringBuilder();

        for (int temp = 0; temp < weakNodes.length; temp++) {
            stringBuilder.append(weakNodes[temp]);
            stringBuilder.append(":");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        return stringBuilder.toString();
    }
}
