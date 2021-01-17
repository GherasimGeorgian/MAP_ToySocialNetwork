package socialnetwork.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MessageAlert {
    static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);
        message.setHeaderText(header);
        if(text.length()> 35){
            int len = text.length();
            String a = text.substring(0, len / 2), b = text.substring(len / 2);
            message.setContentText(a + '\n' + b);
        }
        else {
            message.setContentText(text);
        }
        message.initOwner(owner);
        message.show();
    }

    static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Mesaj eroare");
        message.setContentText(text);
        message.show();
    }
}
