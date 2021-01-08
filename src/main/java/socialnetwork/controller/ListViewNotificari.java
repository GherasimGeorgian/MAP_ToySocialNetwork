package socialnetwork.controller;

import com.itextpdf.text.Image;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import javax.swing.text.html.ImageView;

public class ListViewNotificari {
    static class Cell extends ListCell<String>{
        HBox hbox = new HBox();
        Button btn = new Button();
        Label label = new Label();
        Pane pane = new Pane();

        public Cell(){
            super();
            hbox.getChildren().addAll(label,pane,btn);
            hbox.setHgrow(pane, Priority.ALWAYS);
        }
    }
}
