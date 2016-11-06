package xmltocsvparser.model;

import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading transferring the data to which the headers point to a CSV-file
 *
 * @author Robin Weitzel
 */
public class CSVHandler {
    private String COMMA_DELIMITER;
    private ArrayList<Integer> differenceInFormatting;
    private String filePath;
    private boolean useExcelLayout;

    /**
     * Standard constructor
     *
     * @param filePath               path to the CSV-file where the data should be saved
     * @param differenceInFormatting [0, infinity[ displaying how many "empty" nodes can be found before each "real" one
     */
    public CSVHandler(String filePath, ArrayList<Integer> differenceInFormatting) {
        this.filePath = filePath;
        this.differenceInFormatting = differenceInFormatting;
        COMMA_DELIMITER = SettingsHandler.getSeperationCharacter();
        useExcelLayout = SettingsHandler.getUseExcelLayout();
    }

    /**
     * Writes the headername and the data the headers point to to a CSV-file
     * Writes one column for each header, separated by the predefined COMMA_DELIMITER
     *
     * @param headers the collection of headers to be read
     * @param paths   The paths to all XML-files where the data is saved
     * @return true if writing was successful, otherwise false
     */
    public boolean writeHeaderToCSV(ObservableList<CustomHeader> headers, List<File> paths) {
        XMLHandler xmlHandler = null;
        StringBuilder stringBuilder = new StringBuilder();
        PrintWriter printWriter = null;


        try {
            String text;
            OutputStream outputStream = new FileOutputStream(filePath);

            if (useExcelLayout) {
                // Excel had issues displaying the CSV correctly. This is used to ensure the file is recognized as UTF-8
                outputStream.write(239);
                outputStream.write(187);
                outputStream.write(191);
            }

            printWriter = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            for (int temp = -1; temp < paths.size(); ) { // Saves a row in a StringBuilder. If temp = -1 the header is written to the file
                stringBuilder.delete(0, stringBuilder.length());
                for (CustomHeader head : headers) {
                    text = head.getText(xmlHandler, temp);
                    text = text.replace("\n", "").replace("\r", "");
                    if (useExcelLayout) {
                        stringBuilder.append("\"" + text + "\"" + COMMA_DELIMITER);
                    } else {
                        stringBuilder.append(text + COMMA_DELIMITER);
                    }
                }

                stringBuilder.append("\n"); // After one row was written moves to the next row

                temp = temp + 1;
                if (temp < paths.size()) { // Because no file is needed for the header, the first file is only read in after the header has been written
                    while (paths.get(temp).getName().startsWith("Schema")) {
                        temp = temp + 1;
                        if (temp >= paths.size()) {
                            break;
                        }
                    }
                    if (temp < paths.size()) {
                        xmlHandler = new XMLHandler(paths.get(temp).getAbsolutePath(), differenceInFormatting.get(temp));
                    }
                }
                printWriter.write(stringBuilder.toString()); // Writes the lines to the file
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            printWriter.close();
        }
        return true;
    }

    /**
     * Gets the longest path of a node in a collection of Headers
     *
     * @param headers a collection of Headers that should be searched
     * @return the length of the longest path
     */
    private int getLargestHeader(ArrayList<CustomHeader> headers) {
        int largestSize = 0;
        int size = 0;
        for (int temp = 0; temp < headers.size(); temp++) {
            size = headers.get(temp).getPaths().size();
            if (size > largestSize) {
                largestSize = size;
            }
        }
        return largestSize;
    }
}
