package xmltocsvparser.model;

import org.w3c.dom.Node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * CustomHeaders can be thought of as a column in a table.
 * Each header has a name (top row in a table) and contains data.
 * A CSV is basically just a table so data can be easily extracted from CustomHeaders to form a CSV-file
 * <p>
 * Instead of storing data, a header only stores directions to where in an XML-file the data can be found.
 * This is used to compare the paths of one XML-file to another one.
 *
 * @author Robin Weitzel
 */
public class CustomHeader implements Serializable {
    public static final long serialVersionUID = -3040096452457271695L;
    private String headerName;
    private ArrayList<CustomPath> paths;
    private CustomPath tempPath;
    private boolean isTrueHeader = true;
    private boolean writeProtected = true; // This identifies, whether the Header can be deleted. Because users can add headers, they also need the option to delete them. Once the XML-file has benn parsed and th user views the next one all old headers become write-protected
    private boolean justSchema = false; // True if Header is only used for matching schema

    /**
     * Used to create false headers
     * In the GUI, the JList should not only contain the real headers but also the option to add new headers.
     * This is done by creating a fake headers (which is displayed in the list) once it is selected, nodes from the left side are automatically added to the JList
     * This headers is _not_ displayed in the final CSV-file
     */
    public CustomHeader() {
        isTrueHeader = false;
        headerName = "Add new Element";
    }

    public CustomHeader(CustomHeader customHeader) {
        this.headerName = customHeader.getHeaderName();
        this.paths = customHeader.getPaths();
        justSchema = true;
    }

    /**
     * Standard constructor
     *
     * @param path path to the first node in this header
     */
    public CustomHeader(CustomPath path) {
        this.headerName = path.getTagName();
        paths = new ArrayList<>(0);
        paths.add(path);
        tempPath = null;
    }

    /**
     * Constructor to create none-writeprotected Headers
     *
     * @param path           Path to the first node
     * @param writeProtected true are false, if header is write-protected. Usually false, otherwise no reason to not use standard-constructor
     */
    public CustomHeader(CustomPath path, boolean writeProtected) {
        this.headerName = path.getTagName();
        paths = new ArrayList<>(0);
        tempPath = path;
        this.writeProtected = writeProtected;
    }

    /**
     * Checks if the header is a true header
     *
     * @return ture, if header is a true header, otherwise false
     */
    public boolean isTrueHeader() {
        return isTrueHeader;
    }

    /**
     * Checks if the header is writeprotected
     *
     * @return true if header is writeprotected, otherwise false
     */
    public boolean isWriteProtected() {
        return writeProtected;
    }

    /**
     * Returns the ArrayList containing the paths
     *
     * @return Arraylist containing the paths
     */
    public ArrayList<CustomPath> getPaths() {
        return paths;
    }

    /**
     * Returns the path at a certain position in the List
     *
     * @param index the path to be retrieved from the ArrayList
     * @return the path found at the position or null, if none was found
     */
    public CustomPath getPath(int index) {
        if (index < paths.size() && index >= 0) {
            return paths.get(index);
        } else {
            return null;
        }
    }

    /**
     * Used to retrieve data according to a path.
     *
     * @param xmlHandler the handler points to the file from which to extract the data
     * @param position   position points at the position in the list of Paths
     * @return The Value saved in the text-file in the XML-file
     */
    public String getText(XMLHandler xmlHandler, int position) {
        Node node = null;
        if (position >= paths.size()) {
            return null;
        } else if (position == -1) { // if position is -1, the header should be returned.
            return headerName;
        } else {
            node = xmlHandler.getNodeByPath(paths.get(position));
            if (node != null) {
                return node.getTextContent();
            } else {
                return "missing";
            }
        }
    }

    /**
     * Returns the headerName
     *
     * @return HeaderName
     */
    public String getHeaderName() {
        return headerName;
    }


    /**
     * * Sets the temp Path.
     * The temp path is used when a Headers is match to a node in a new XML-file.
     * Once the user confirms the matching (by pressing ok), the temporary node is added to the  permanent paths
     *
     * @param tempPath the path to a Node that is matched to this headers
     */
    public void setTempPath(CustomPath tempPath) {
        this.tempPath = tempPath;
    }

    /**
     * Converts a temp-path to a permanent one (adding it to the paths list and emptying temp-path)
     * This is done once the user confirms the matching
     *
     * @return true if a temp-path existed, otherwise false
     */
    public boolean convertTempPath() {
        if (tempPath == null) {
            return false;
        } else {
            paths.add(tempPath);
            tempPath = null;
            writeProtected = true;
            return true;
        }
    }

    /**
     * Clears the temp path
     */
    public void removeTempPath() {
        this.tempPath = null;
    }

    /**
     * Used when displaying the header in a JList.
     * Returns the headername. If a temp-path exists returns it as well.
     *
     * @return String containing headerName and Tag-Name of temp-path
     */
    public String toString() {
        String displayName = "";
        if (tempPath != null) {
            displayName = headerName + " <" + tempPath.getTagName() + ">";
        } else {
            displayName = headerName;
        }
        return displayName;
    }

    /**
     * Ensures a header matches a fixed size.
     * Size means the size of the Arraylist containing the paths.
     * Since since paths are matched to files in order they were read, it is important tha all headers have the same size (the amount of XML-file read in -1)
     * In case a header does not match the size, blanks are added.
     * <p>
     * Because the {@link xmltocsv.GUI} makes sure, that blanks are added if a node is not matched to a header this can only happen, if a header is created after some XML-files have already been read in.
     * Therefore, blanks need to be added to the beginning.
     *
     * @param size
     */
    public void matchHeaderToSize(int size) {
        if (size <= paths.size()) { // if the size of the header is already correct.
            return;
        } else { // if the header is to small
            Collections.reverse(paths); // Because the blanks need to be added to the beginning, the Collection is reversed.
            for (int temp = 0; temp < size; temp++) { // adds the blanks
                if (temp >= paths.size()) {
                    paths.add(new CustomPath(false));
                }
            }
            Collections.reverse(paths); // returns the Collection to its original order
        }
    }

    /**
     * Adds a blank path to the paths-collection
     */
    public void addBlank() {
        paths.add(new CustomPath(false));
    }

    /**
     * Copies a path at a certain position to the temp-path position
     *
     * @param index the path to be copied
     */
    public void duplicatePath(int index) {
        tempPath = paths.get(index);
    }

    public boolean isJustSchema() {
        return justSchema;
    }

    public boolean hasTempPath() {
        return tempPath != null;
    }
}
