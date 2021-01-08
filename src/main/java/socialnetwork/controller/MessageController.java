package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import socialnetwork.domain.Invite;
import socialnetwork.domain.Message;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceException;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.observer.Observer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class MessageController implements Observer<ChangeEvent> {
    private UserServiceFullDB service;
    private Utilizator user_app=null;
    private Utilizator user_comunicate=null;
    private ConversationView cv;
    private Button sendMessageButton = new Button("Send");


    private SpeechBox selectedMessage = null;
    private Button replyMessage = new Button("reply");

    private Background DEFAULT_SENDER_BACKGROUND, DEFAULT_RECEIVER_BACKGROUND;

    private int CurrentMessageIndex = 0;
    private int sizeMessages = 0;


    private TextField userInput = new TextField();
    private List<Utilizator> usersTo;
    List<AbstractMap.SimpleImmutableEntry<String, String>> useriSimpleImmutable= new ArrayList<>();
    ObservableList<Message> modelUserMessages = FXCollections.observableArrayList();

    public void setService(UserServiceFullDB service,Utilizator userApp, Utilizator user_com) {
        this.service=service;
        this.user_app = userApp;
        this.user_comunicate = user_com;
        service.addObserver(this);
        useriSimpleImmutable.add(new AbstractMap.SimpleImmutableEntry<>(user_comunicate.getFirstName(), user_comunicate.getLastName()));
        cv = new ConversationView(user_comunicate.getFirstName() + " " + user_comunicate.getLastName(),user_app.getFirstName() + " " + user_app.getLastName(),sendMessageButton,replyMessage,userInput);
        vBox.getChildren().add(cv);
        initModel();
        loadMessages();

    }
    public void setServiceMultipleUsers(UserServiceFullDB service,Utilizator userApp, List<Utilizator> useri){
        this.service=service;
        this.user_app=userApp;
        this.usersTo = useri;
        for(Utilizator utilizator: useri) {
            useriSimpleImmutable.add(new AbstractMap.SimpleImmutableEntry<>(utilizator.getFirstName(), utilizator.getLastName()));
        }
        cv = new ConversationView("MultipleUsers",user_app.getFirstName() + " " + user_app.getLastName(),sendMessageButton,replyMessage,userInput);
        vBox.getChildren().add(cv);
        initModelMultipleUsers();
        loadMultipleMessages();

    }

    @Override
    public void update(ChangeEvent event) {
        ChangeEventType t = event.getType();
        //daca se modifica un mesaj
        if(t == ChangeEventType.M_ADD) {
             Message msg = (Message)event.getData();
             if(msg.getTo().contains(user_app)){
                 if(msg instanceof ReplyMessage) {
                     ReplyMessage rplmsg = (ReplyMessage)msg;
                     cv.receiveMessage(rplmsg.toString(),rplmsg.getId());
                 }
                 else{
                     cv.receiveMessage(msg.getMessage(),msg.getId());
                 }
             }
        }
    }

    @FXML
    private VBox vBox;

    @FXML
    public void initialize() {

        vBox.setOnKeyPressed(e -> {
            sizeMessages = cv.getSizeMessages();
            // down mergem in sus
            if (e.getCode() == KeyCode.D) {



                if(CurrentMessageIndex > 0){
                    //culoare pen-ultim
                    SpeechBox xx2 = (SpeechBox) cv.getPosition(CurrentMessageIndex-1);
                    xx2.setBackGroundColor(xx2);
                }


                System.out.println(sizeMessages + " " + CurrentMessageIndex);
                if(CurrentMessageIndex <= sizeMessages -2) {


                    Color DEFAULT_SENDER_COLOR = Color.RED;
                    Color DEFAULT_RECEIVER_COLOR = Color.RED;
                    DEFAULT_SENDER_BACKGROUND = new Background(
                            new BackgroundFill(DEFAULT_SENDER_COLOR, new CornerRadii(5, 0, 5, 5, false), Insets.EMPTY));
                    DEFAULT_RECEIVER_BACKGROUND = new Background(
                            new BackgroundFill(DEFAULT_RECEIVER_COLOR, new CornerRadii(0, 5, 5, 5, false), Insets.EMPTY));


                    SpeechBox xx = (SpeechBox) cv.getPosition(CurrentMessageIndex);
                    selectedMessage = xx;
                    xx.getLabel().setBackground(DEFAULT_SENDER_BACKGROUND);
                    xx.getDirectionIndicator().setFill(DEFAULT_RECEIVER_COLOR);
                    CurrentMessageIndex++;
                }
            }

            if (e.getCode() == KeyCode.A) {
                System.out.println(sizeMessages + " " + CurrentMessageIndex);

                if(CurrentMessageIndex > 0) {
                    CurrentMessageIndex--;



                    SpeechBox xx2 = (SpeechBox) cv.getPosition(CurrentMessageIndex+1);
                    xx2.setBackGroundColor(xx2);


                    Color DEFAULT_SENDER_COLOR = Color.RED;
                    Color DEFAULT_RECEIVER_COLOR = Color.RED;
                    DEFAULT_SENDER_BACKGROUND = new Background(
                            new BackgroundFill(DEFAULT_SENDER_COLOR, new CornerRadii(5, 0, 5, 5, false), Insets.EMPTY));
                    DEFAULT_RECEIVER_BACKGROUND = new Background(
                            new BackgroundFill(DEFAULT_RECEIVER_COLOR, new CornerRadii(0, 5, 5, 5, false), Insets.EMPTY));


                    SpeechBox xx = (SpeechBox) cv.getPosition(CurrentMessageIndex);
                    selectedMessage = xx;
                    xx.getLabel().setBackground(DEFAULT_SENDER_BACKGROUND);
                    xx.getDirectionIndicator().setFill(DEFAULT_RECEIVER_COLOR);

                }
            }
        });


        sendMessageButton.setOnAction(event-> {


            Message msg = service.trimiteMesaj(user_app.getFirstName(), user_app.getLastName(),useriSimpleImmutable,userInput.getText(),(long)-1);


            cv.sendMessage(userInput.getText(),msg.getId());

            //TODO
            //reply message
            //tratam doar cazul mesaj
            //adaugam in baza de date mesajul
              //

            userInput.setText("");
        });


        replyMessage.setOnAction(event-> {
        if(selectedMessage == null){
            selectedMessage = null;
            MessageAlert.showErrorMessage(null, "Trebuie sa selectezi un mesaj pentru un reply!");
        }else {
            if(!userInput.getText().trim().isEmpty()) {
                ReplyMessage msg = (ReplyMessage) service.trimiteMesaj(user_app.getFirstName(), user_app.getLastName(),useriSimpleImmutable,userInput.getText(),selectedMessage.getIdMessage());
                cv.sendMessage(msg.toString(),msg.getId());
                userInput.setText("");
            }
            else{
                selectedMessage = null;
                MessageAlert.showErrorMessage(null,"Trebuie sa introduci un mesaj!");
            }
        }

        });
    }
    private void initModel() {
        modelUserMessages.setAll(service.afisareConversatii(user_app.getFirstName(), user_app.getLastName(), user_comunicate.getFirstName(),user_comunicate.getLastName()));

    }
    private void initModelMultipleUsers() {
        modelUserMessages.setAll(service.afisareConversatiiMultipleUsers(user_app.getFirstName(), user_app.getLastName(), usersTo));

    }


    public void loadMessages(){
         //load messages one user
        for(Message msg: modelUserMessages){
            if(msg instanceof ReplyMessage) {
                ReplyMessage rplmsg = (ReplyMessage)msg;

                if (msg.getFrom().getId().toString().equals(user_app.getId().toString())) {
                    cv.sendMessage(rplmsg.toString(),rplmsg.getId());
                } else {
                    cv.receiveMessage(rplmsg.toString(),rplmsg.getId());
                }
            }
            else{
                if (msg.getFrom().getId().toString().equals(user_app.getId().toString())) {
                    cv.sendMessage(msg.getMessage(),msg.getId());
                } else {
                    cv.receiveMessage(msg.getMessage(),msg.getId());
                }
            }
        }
    }

    public void loadMultipleMessages(){
        //load messages one user
        for(Message msg: modelUserMessages){
            if(msg instanceof ReplyMessage) {
                ReplyMessage rplmsg = (ReplyMessage)msg;

                if (msg.getFrom().getId().toString().equals(user_app.getId().toString())) {
                    cv.sendMessage(rplmsg.toString(),rplmsg.getId());
                } else {
                    cv.receiveMessage(rplmsg.toString(),rplmsg.getId());
                }
            }
            else{
                if (msg.getFrom().getId().toString().equals(user_app.getId().toString())) {
                    cv.sendMessage(msg.getMessage(),msg.getId());
                } else {
                    cv.receiveMessage(msg.getMessage(),msg.getId());
                }
            }
        }
    }



}
