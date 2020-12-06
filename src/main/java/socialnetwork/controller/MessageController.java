package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

public class MessageController {
    private UserServiceFullDB service;
    private Utilizator user_app=null;
    private Utilizator user_comunicate=null;
    private ConversationView cv;
    ObservableList<Message> modelUserMessages = FXCollections.observableArrayList();

    public void setService(UserServiceFullDB service,Utilizator userApp, Utilizator user_com) {
        this.service=service;
        this.user_app = userApp;
        this.user_comunicate = user_com;
        cv = new ConversationView(user_comunicate.getFirstName() + " " + user_comunicate.getLastName(),user_app.getFirstName() + " " + user_app.getLastName());
        vBox.getChildren().add(cv);
        cv.sendMessage("ceva");
        cv.receiveMessage("ei da?");
        initModel();
        loadMessages();
    }

    @FXML
    private VBox vBox;

    @FXML
    public void initialize() {

    }
    private void initModel() {
        modelUserMessages.setAll(service.afisareConversatii(user_app.getFirstName(), user_app.getLastName(), user_comunicate.getFirstName(),user_comunicate.getLastName()));

    }
    public void loadMessages(){
        for(Message msg: modelUserMessages){
            if(msg.getFrom().getId().toString().equals(user_app.getId().toString())){
                cv.sendMessage(msg.getMessage());
            }
            else{
                cv.receiveMessage(msg.getMessage());
            }
        }
    }


}
