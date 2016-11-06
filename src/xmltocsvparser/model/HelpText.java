package xmltocsvparser.model;

/**
 * Created by Robin on 06.11.2016.
 */
public class HelpText {

    private String name;
    private String[] keyWords;
    private String text;

    public HelpText(String name, String[] keyWords, String text) {
        this.name = name;
        this.keyWords = keyWords;
        this.text = text;
    }

    public boolean checkKeyword(String keyword) {
        for (int temp = 0; temp < keyWords.length; temp++) {
            if (keyWords[temp].equals(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return name;
    }

    public String getHTML() {
        String title = "<h2>" + this.name + "</h2>\n";
        String text = "<p>" + this.text + "</p>";

        return title + text;
    }
}
