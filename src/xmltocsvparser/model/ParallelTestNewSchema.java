package xmltocsvparser.model;

import javafx.collections.ObservableList;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Used for multi-Threaded test if a schema fits an XML-file
 *
 * @author Robin Weitzel
 */
public class ParallelTestNewSchema implements Callable<Double> {

    String pathToXML;
    // For meaning of variables see GUI-Method
    private ArrayList<CustomHeader> header;
    private XMLHandler xmlHandler;
    private double counter = 0, sucess = 0;
    private int index;
    private boolean useWeakPaths, useBranchAndBound = true;

    /**
     * Creates a new Class-Object
     * All parameters need to be passed through class-creation because call()-Method cant take parameters
     *
     * @param header            The list of headers which contain the schema
     * @param pathToXML         the path to the XMLfile to which the schema should be applied
     * @param index             the schema which should be used (corresponds to a file)
     * @param offset            The offset the new XML-file has
     */
    public ParallelTestNewSchema(ObservableList<CustomHeader> header, String pathToXML, int index, int offset) {
        this.xmlHandler = new XMLHandler(pathToXML, offset);
        this.header = new ArrayList<>(header);
        this.index = index;
        this.useWeakPaths = SettingsHandler.getUseWeakPaths();
        this.pathToXML = pathToXML;
    }

    /**
     * This method is called once the thread is started
     * Begins recursively applying the schema and returns a successrate
     *
     * @return value between 0 and 1 (1 = perfect match, 0 = no match)
     * @Override
     */
    public Double call() {
        if (useBranchAndBound) { // If branch-Mode is used
            startRecursiveTestSchema();
        } else { // If branch-Mode is not used
            int size = getLargestHeader(header);
            CustomPath path, newPath;
            Node node;

            for (int temp = 0; temp < header.size(); temp++) {
                header.get(temp).matchHeaderToSize(size);
                path = header.get(temp).getPath(index);

                counter = counter + 1;
                if (useWeakPaths) { // If weak-paths can be used
                    newPath = xmlHandler.startRecursiveGetNodeByWeakPath(path);
                    if (newPath != null) {
                        sucess = sucess + 1;
                    }
                } else { // If weak paths cant be used
                    node = xmlHandler.getNodeByPath(path);
                    if (node != null) {
                        sucess = sucess + 1;
                    }
                }
            }
        }
        return sucess / counter;
    }

    /**
     * Gets the longests path of a node in a collection of Headers
     * Same method as {@link (ArrayList)}
     *
     * @param headers a collection of Headers that should be searched
     * @return the length of the longest path
     */
    private int getLargestHeader(ArrayList<CustomHeader> headers) {
        int largestSize = 0;
        int size;
        for (int temp = 0; temp < headers.size(); temp++) {
            size = headers.get(temp).getPaths().size();
            if (size > largestSize) {
                largestSize = size;
            }
        }
        return largestSize;
    }

    /**
     * Start the recursive testing of a schema
     */
    private void startRecursiveTestSchema() {

        counter = header.size();
        if (counter == 0) {
            sucess = counter;
        } else {
            CustomPath path = header.get(0).getPath(index);
            if (path.isTruePath()) {
                int[] array = {path.getNodes()[0]};
                String[] array2 = {path.getWeakNodes()[0]};
                path = new CustomPath(array, array2, array2[0]);
                recursiveTestSchema(header, path);
            }
        }
    }

    /**
     * For idea see {@link (ArrayList, CustomPath, int)}
     *
     * @param headers the collection of Headers to be tested
     * @param path    the path to the parent
     */
    private void recursiveTestSchema(ArrayList<CustomHeader> headers, CustomPath path) {

        ArrayList<ArrayList<CustomHeader>> headerContainer = new ArrayList<>(0);
        ArrayList<Integer> positionalMapping = new ArrayList<>(0);
        ArrayList<CustomPath> pathCollection;
        CustomPath newPath;
        Node node;
        int pathLength = path.getNodes().length, position = 0, foundPosition;
        int[] nodes;

        for (int temp = 0; temp < headers.size(); temp++) {
            newPath = headers.get(temp).getPath(index);
            if (newPath.isTruePath()) {
                nodes = newPath.getNodes();
                if (nodes != null) {
                    if (pathLength == nodes.length) { //If the path is as long as the path of the header (meaning we have reached the node that headers.get(temp) points to)
                        node = xmlHandler.getNodeByPath(path);
                        if (node != null) {
                            sucess = sucess + 1;
                        }
                    } else if (pathLength == nodes.length - 1) { //If the path is as long as the path of the header (meaning we have reached the node that headers.get(temp) points to)
                        node = xmlHandler.getNodeByPath(new CustomPath(path, nodes[nodes.length - 1], headers.get(temp).getPath(index).getWeakNodes()[nodes.length - 1]));
                        if (node != null) {
                            sucess = sucess + 1;
                        }
                    } else {
                        if (!positionalMapping.contains(nodes[pathLength])) {
                            headerContainer.add(new ArrayList<CustomHeader>(0));
                            positionalMapping.add(nodes[pathLength]);
                            headerContainer.get(position).add(headers.get(temp));
                            position = position + 1;
                        } else {
                            foundPosition = positionalMapping.indexOf(nodes[pathLength]);
                            headerContainer.get(foundPosition).add(headers.get(temp));
                        }
                    }
                }
            }
        }

        for (int temp = 0; temp < positionalMapping.size(); temp++) {
            pathCollection = xmlHandler.parallelSearchHelper(path, positionalMapping.get(temp), headerContainer.get(temp).get(0).getPath(index).getWeakNodes()[pathLength]);
            for (int temp2 = 0; temp2 < pathCollection.size(); temp2++) {
                recursiveTestSchema(headerContainer.get(temp), pathCollection.get(temp2));
            }
        }
    }

}
