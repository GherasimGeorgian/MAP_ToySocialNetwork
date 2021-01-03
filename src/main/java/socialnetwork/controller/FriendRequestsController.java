package socialnetwork.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.*;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController implements Observer<ChangeEvent> {
    private UserServiceFullDB service;
    private Utilizator current_user = null;


    ObservableList<Invite> modelInvite = FXCollections.observableArrayList();
    ObservableList<Invite> modelInviteToUsers = FXCollections.observableArrayList();
    @FXML
    TableView<Invite> tableViewInvite;



    @FXML
    TableColumn<Invite, String> tableColumnFirstNameTo;

    @FXML
    TableColumn<Invite, String> tableColumnLastNameTo;

    @FXML
    TableColumn<Invite, LocalDateTime> tableColumnDate;

    @FXML
    TableColumn<Invite, String> tableColumnStatus;


    @FXML
    TableView<Invite> tableViewInviteRecieve;

    @FXML
    TableColumn<Invite, String> tableColumnFirstNameToRec;

    @FXML
    TableColumn<Invite, String> tableColumnLastNameToRec;

    @FXML
    TableColumn<Invite, LocalDateTime> tableColumnDateRec;

    @FXML
    TableColumn<Invite, String> tableColumnStatusRec;

    //buttons

    @FXML
    Button btnCancelInvite;

    @FXML
    Button btnAcceptInvite;


    public void setService(UserServiceFullDB service, Utilizator user) {
        this.current_user = user;
        this.service=service;
        service.addObserver(this);
        initModel();
    }

    private void initModel() {
        Iterable<Invite> invites = service.allinvitationsByUser(current_user.getFirstName(),current_user.getLastName());
        List<Invite> invitesList = StreamSupport.stream(invites.spliterator(),false)
                .collect(Collectors.toList());
        modelInvite.setAll(invitesList);

        Iterable<Invite> invitesToUsers = service.allinvitationsToUsers(current_user.getFirstName(),current_user.getLastName());
        List<Invite> invitesListToUsers = StreamSupport.stream(invitesToUsers.spliterator(),false)
                .collect(Collectors.toList());
        modelInviteToUsers.setAll(invitesListToUsers);

    }
    @Override
    public void update(ChangeEvent inviteChangeEvent) {

        ChangeEventType t = inviteChangeEvent.getType();
        if(t == ChangeEventType.I_ADD || t == ChangeEventType.I_DEL || t == ChangeEventType.I_UPD ||
                t == ChangeEventType.P_ADD || t == ChangeEventType.P_DEL || t == ChangeEventType.P_UPD) {
            initModel();
        }
    }

    @FXML
    public void initialize() {

        tableViewInviteRecieve.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableViewInvite.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableColumnFirstNameTo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getToInvite().getFirstName()));
        tableColumnLastNameTo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getToInvite().getLastName()));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<Invite,LocalDateTime>("dateInvite"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<Invite, String>("status"));

        tableViewInvite.setItems(modelInvite);


        tableColumnFirstNameToRec.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFromInvite().getFirstName()));
        tableColumnLastNameToRec.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFromInvite().getLastName()));
        tableColumnDateRec.setCellValueFactory(new PropertyValueFactory<Invite,LocalDateTime>("dateInvite"));
        tableColumnStatusRec.setCellValueFactory(new PropertyValueFactory<Invite, String>("status"));

        tableViewInviteRecieve.setItems(modelInviteToUsers);
    }

    public void handleCancelInvite(ActionEvent actionEvent) {
        Invite selected = tableViewInvite.getSelectionModel().getSelectedItem();
        if(selected != null) {
            if(selected.getStatus() == InviteStatus.PENDING) {
                try {
                    Invite deleted = service.stergeInvitatie(selected.getId());
                    if (deleted == null) {

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Stergere");
                        alert.setHeaderText(null);
                        alert.setContentText("S-a sters cu succes!");
                        alert.showAndWait();

                    }
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


    public void handleAcceptInvite(ActionEvent actionEvent) {
        Invite selected = tableViewInviteRecieve.getSelectionModel().getSelectedItem();
        if(selected != null) {
            if(selected.getStatus() == InviteStatus.PENDING) {
                try {
                     service.acceptaInvitatie(selected.getId());
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
}
