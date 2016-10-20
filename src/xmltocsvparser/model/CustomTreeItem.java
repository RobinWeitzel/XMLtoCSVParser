package xmltocsvparser.model;

import javafx.scene.control.TreeItem;

/**
 * Created by Robin on 19.10.2016.
 */
public class CustomTreeItem<T> extends TreeItem<T> {

    private int id;
    private String textContent;

    public CustomTreeItem() {
        id = -1;
    }

    public CustomTreeItem(final T value, int id) {
        super(value);
        this.id = id;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public int getId() {
        return id;
    }
}
