package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import socialnetwork.domain.AbonareEveniment;
import socialnetwork.domain.Eveniment;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateEventController {

    private UserServiceFullDB service;
    private Utilizator user_app;
    public void setService(UserServiceFullDB service, Utilizator user) {
        this.user_app = user;
        this.service=service;
    }

    @FXML
    Button btnCreateEvent;

    @FXML
    Label lblNameEvent;

    @FXML
    TextField txtNameEvent;

    @FXML
    DatePicker datePickerEvent;
    @FXML
    public void initialize() {
        btnCreateEvent.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(datePickerEvent.getValue() != null) {
                    Eveniment eventul = service.createEvent(txtNameEvent.getText(), datePickerEvent.getValue().atStartOfDay());
                    AbonareEveniment abonare =  service.abonareEveniment(eventul,user_app);
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Nice", "Evenimentul a fost salvat cu succes!!!");
                }
                else{
                    MessageAlert.showErrorMessage(null,"Trebuie sa alegi odata");
                }

            }
        });
    }

}
