package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.Invite;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

import java.time.LocalDateTime;

public class FriendRequestsController {
    private UserServiceFullDB service;
    private Utilizator current_user = null;

//
//    @FXML
//    TableView<Invite> tableViewInvite;
//
//    @FXML
//    TableColumn<Invite, String> tableColumnFirstNameTo;
//
//    @FXML
//    TableColumn<Invite, String> tableColumnLastNameTo;
//
//    @FXML
//    TableColumn<Invite, LocalDateTime> tableColumnDate;
//
//    @FXML
//    TableColumn<Invite, String> tableColumnStatus;
//
//
    public void setService(UserServiceFullDB service, Utilizator user) {
        this.current_user = user;
        this.service=service;
    }
//
//    @FXML
//    public void initialize() {
//        tableColumnFirstNameTo.setCellValueFactory(param -> param.getValue().getStatus().toString());
//        tableColumnLastNameTo.setCellValueFactory(new PropertyValueFactory<Invite, String>("toInvite"));
//        tableColumnDate.setCellValueFactory(new PropertyValueFactory<Invite, LocalDateTime>("dateInvite"));
//        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<Invite, String>("status"));
//
//        tableViewInvite.setItems(modelGrade);
//
//    }
}
