package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import socialnetwork.domain.Account;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

import java.time.LocalDateTime;

public class CreateAccountController {
    private UserServiceFullDB service;
    private Utilizator user_app;
    public void setService(UserServiceFullDB service) {
        this.service=service;
    }

    @FXML
    Button btnCreate;

    @FXML
    Button btnCancel;
    @FXML
    TextField txtNume;

    @FXML
    TextField txtPrenume;

    @FXML
    TextField txtEmail;

    @FXML
    PasswordField txtParola;

    @FXML
    PasswordField txtConfirmParola;


    @FXML
    CheckBox checkBoxCondition;

    @FXML
    public void initialize() {
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) btnCancel.getScene().getWindow();
                stage.close();
            }
        });


        btnCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!txtNume.getText().isEmpty() && !txtPrenume.getText().isEmpty() && !txtEmail.getText().isEmpty()
                && !txtParola.getText().isEmpty() && !txtConfirmParola.getText().isEmpty() && checkBoxCondition.isSelected())
                {
                    if(txtParola.getText().equals(txtConfirmParola.getText())) {
                        Account accRez = service.createAccount(txtNume.getText(),txtPrenume.getText(),txtEmail.getText(),txtParola.getText(),"NORMAL");

                        if (accRez == null) {
                            MessageAlert.showErrorMessage(null,"Contul exista deja!");
                            Stage stage = (Stage) btnCancel.getScene().getWindow();
                            stage.close();
                        } else {
                            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Nice", "Contul tau a fost creat cu succes!!!");
                            Stage stage = (Stage) btnCancel.getScene().getWindow();
                            stage.close();
                        }
                    }
                    else{
                        MessageAlert.showErrorMessage(null,"Ai introdus parola gresit!");
                        Stage stage = (Stage) btnCancel.getScene().getWindow();
                        stage.close();
                    }
                }
                else{
                  MessageAlert.showErrorMessage(null,"Trebuie sa completezi toate informatiile!");
                    Stage stage = (Stage) btnCancel.getScene().getWindow();
                    stage.close();
                }

            }
        });
    }
}
