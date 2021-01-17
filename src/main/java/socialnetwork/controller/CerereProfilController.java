package socialnetwork.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.*;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.observer.Observer;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CerereProfilController implements Observer<ChangeEvent>
{

    private UserServiceFullDB service;
    private Utilizator user_app;

    public void setService(UserServiceFullDB Userservice, Utilizator userConn) {
        this.service = Userservice;
        this.user_app = userConn;
        service.addObserver(this);
        initModelFriendsRequests();
        setImageViewFriend();

    }
    ObservableList<Invite> modelInvite = FXCollections.observableArrayList();

    @FXML
    TableView<Invite> tableViewInvite2;
    @FXML
    TableColumn<Invite, String> tableColumnFirstNameToRec2;

    @FXML
    TableColumn<Invite, String> tableColumnLastNameToRec2;

    @FXML
    Button btnRejectInvite;

    @FXML
    ImageView imageViewFriend;

    private void initModelFriendsRequests() {
        Iterable<Invite> invites = service.allinvitationsToUsers(user_app.getFirstName(),user_app.getLastName());
        List<Invite> invitesList = StreamSupport.stream(invites.spliterator(),false)
                .collect(Collectors.toList());
        modelInvite.setAll(invitesList);
    }
    @Override
    public void update(ChangeEvent event) {

        ChangeEventType t = event.getType();
        if (t == ChangeEventType.I_ADD || t == ChangeEventType.I_DEL || t == ChangeEventType.I_UPD ||
                t == ChangeEventType.P_ADD || t == ChangeEventType.P_DEL || t == ChangeEventType.P_UPD) {
            initModelFriendsRequests();
        }
    }

    public void handleAcceptInvite(ActionEvent actionEvent) {
        Invite selected = tableViewInvite2.getSelectionModel().getSelectedItem();
        if(selected != null) {
            if(selected.getStatus() == InviteStatus.PENDING) {
                try {
                    service.acceptaInvitatie(selected.getId());
                    setImageViewFriend();
                } catch (Exception e) {

                }

            }
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Atentie");
                alert.setHeaderText(null);
                alert.setContentText("Invitatia trebuie sa fie Pending!");
                alert.showAndWait();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Atentie");
            alert.setHeaderText(null);
            alert.setContentText("Nu ai selectat nimic!");
            alert.showAndWait();
        }
    }

    public void setImageViewFriend(){
        try{

            File file1 = new File("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/profile.jpg");
            Image a = new Image(file1.toURI().toURL().toExternalForm());
            imageViewFriend.setImage(a);

        }catch(Exception ex){

        }

    }
    @FXML
    public void initialize(){


        tableViewInvite2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableColumnFirstNameToRec2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFromInvite().getFirstName()));
        tableColumnLastNameToRec2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFromInvite().getLastName()));
        tableViewInvite2.setItems(modelInvite);


        btnRejectInvite.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Invite selected = tableViewInvite2.getSelectionModel().getSelectedItem();
                if(selected != null) {
                    if(selected.getStatus() == InviteStatus.PENDING) {
                        try {
                            service.rejectInvite(selected);
                            setImageViewFriend();
                        } catch (Exception e) {

                        }

                    }
                    else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Atentie");
                        alert.setHeaderText(null);
                        alert.setContentText("Invitatia trebuie sa fie Pending!");
                        alert.showAndWait();
                    }
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Atentie");
                    alert.setHeaderText(null);
                    alert.setContentText("Nu ai selectat nimic!");
                    alert.showAndWait();
                }
            }
        });

        tableViewInvite2.setRowFactory( tv -> {
            TableRow<Invite> row = new TableRow<>();
            row.setOnMouseClicked(event -> {

                    Invite rowData = row.getItem();
                    Account acc = service.getAccountByUserId(rowData.getFromInvite().getId());

                    try{

                        if(acc.getUrl_photo().equals("null")){
                            File file1 = new File("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/profile.jpg");
                            Image a = new Image(file1.toURI().toURL().toExternalForm());
                            imageViewFriend.setImage(a);
                        }else{
                            File file1 = new File(acc.getUrl_photo());
                            Image a = new Image(file1.toURI().toURL().toExternalForm());
                            imageViewFriend.setImage(a);
                        }



            }catch(Exception ex){

            }

            });
            return row ;
        });

    }
}
