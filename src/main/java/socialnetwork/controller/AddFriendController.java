package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.Invite;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static socialnetwork.service.UserServiceFullDB.toSingleton;

public class AddFriendController {

    private UserServiceFullDB service;

    private  List<Utilizator> notFriends;
    private Utilizator current_user = null;
    private Utilizator selected_user= null;
    public void setService(UserServiceFullDB service,Utilizator user) {
        this.current_user = user;
        this.service=service;
        notFriends = service.notRelatiiUser(current_user.getFirstName(),current_user.getLastName());
    }

    //textfields
    @FXML
    TextField textFieldSearchFriend;

    //labels
    @FXML
    Label lblUser;


    //buttons
    @FXML
    Button btnSendInvite;

    @FXML
    public void initialize() {


        textFieldSearchFriend.textProperty().addListener(x->handleFilterFriends());


        btnSendInvite.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(selected_user != null) {
                    //verificam daca exista deja o invitatie
                    Invite inv = service.findInvitebytwoUsers(current_user.getFirstName(), current_user.getLastName(),selected_user.getFirstName(),selected_user.getLastName());
                    if (inv!= null) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Atentie");
                        alert.setHeaderText(null);
                        alert.setContentText("Exista deja o invitatie catre userul: " + selected_user.getFirstName() + " " + selected_user.getLastName());
                        alert.showAndWait();

                    }
                    // altfel cream o noua invitatie
                    else{
                        service.trimiteInvitatie(current_user.getFirstName(), current_user.getLastName(),selected_user.getFirstName(),selected_user.getLastName());
                    }
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Atentie");
                    alert.setHeaderText(null);
                    alert.setContentText("Nu s-a ales un user caruia sa i se trimita invitatia!");
                    alert.showAndWait();
                }
            }
        });
    }

    private void handleFilterFriends() {

        Predicate<Utilizator> numePredicate = x->x.getLastName().startsWith(textFieldSearchFriend.getText());

        try {
            Utilizator user_gasit = StreamSupport.stream(notFriends.spliterator(), false)
                    .filter(numePredicate)
                    .collect(Collectors.toList()).get(0);
            setlblUser(user_gasit.getFirstName() + " " + user_gasit.getLastName());
            selected_user = user_gasit;
        }catch(Exception ex){
            setlblUser("Nimic gasit");
        }
//        modelGrade.setAll(getNotaDTOList()
//                .stream()
//                .filter(numePredicate.and(temaPredicate).and(notaPredicate))
//                .collect(Collectors.toList())
//        );
    }
    private void setlblUser(String text)
    {
        lblUser.setText(text);
    }
}
