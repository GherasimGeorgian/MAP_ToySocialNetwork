package socialnetwork.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.*;
import socialnetwork.service.InviteServiceFullDB;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.events.InviteChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController implements Observer<InviteChangeEvent> {
    private UserServiceFullDB service;
    private Utilizator current_user = null;
    private InviteServiceFullDB serviceInvite;


    ObservableList<Invite> modelInvite = FXCollections.observableArrayList();
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


    //buttons

    @FXML
    Button btnCancelInvite;


    public void setService(UserServiceFullDB service,InviteServiceFullDB invService, Utilizator user) {
        this.current_user = user;
        this.service=service;
        serviceInvite = invService;
        serviceInvite.addObserver(this);
        initModel();
    }

    private void initModel() {
        Iterable<Invite> invites = serviceInvite.allinvitationsByUser(current_user.getFirstName(),current_user.getLastName());
        List<Invite> invitesList = StreamSupport.stream(invites.spliterator(),false)
                .collect(Collectors.toList());
        modelInvite.setAll(invitesList);
    }
    @Override
    public void update(InviteChangeEvent inviteChangeEvent) {
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstNameTo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getToInvite().getFirstName()));
        tableColumnLastNameTo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getToInvite().getLastName()));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<Invite,LocalDateTime>("dateInvite"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<Invite, String>("status"));

        tableViewInvite.setItems(modelInvite);
    }

    public void handleCancelInvite(ActionEvent actionEvent) {
        Invite selected = tableViewInvite.getSelectionModel().getSelectedItem();
        if(selected != null) {
            if(selected.getStatus() == InviteStatus.PENDING) {
                try {
                    Invite deleted = serviceInvite.stergeInvitatie(selected.getId());
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
}
