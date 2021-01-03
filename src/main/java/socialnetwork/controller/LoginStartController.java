package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import socialnetwork.domain.Account;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

public class LoginStartController {
    private UserServiceFullDB service;
    private Utilizator user_app=null;

    public void setService(UserServiceFullDB service) {
        this.service=service;
    }
    //textfields
    @FXML
    TextField textFieldEmail;
    @FXML
    PasswordField textFieldPassword;


    //buttons
    @FXML
    Button btnLogin;

    @FXML
    Button btnCancel;


    @FXML
    Button btnCreateNewAccount;

    //cava
    int ceva;
    @FXML
    public void initialize() {



        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(textFieldEmail.getText().isEmpty() || textFieldPassword.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Emailul si parola nu trebuie sa fie nule!");
                    alert.showAndWait();
                }
                else{
                    user_app = loginUserbyEmailAndPassword(textFieldEmail.getText(),textFieldPassword.getText());
                    if(user_app == null){
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText(null);
                        alert.setContentText("Userul introdus nu exista!");
                        alert.showAndWait();
                        textFieldEmail.clear();
                        textFieldPassword.clear();
                    }
                    else{
                        try {

                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/views/userpage.fxml"));
                            AnchorPane root=fxmlLoader.load();

                            UserPageController ctrl =fxmlLoader.getController();
                            ctrl.setService(service,user_app);

                            Stage stagePageUser = new Stage();
                            Scene scene = new Scene(root, 1000, 500);

                            stagePageUser.setTitle("UserPage");
                            stagePageUser.setScene(scene);
                            stagePageUser.show();
                            Stage stage = (Stage) btnLogin.getScene().getWindow();
                            stage.close();


                        }catch(Exception ex){
                            System.out.println(ex);
                        }
                    }
                }
            }
        });

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) btnCancel.getScene().getWindow();
                stage.close();
            }
        });


        btnCreateNewAccount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/create_account.fxml"));
                    AnchorPane root=fxmlLoader.load();

                    CreateAccountController ctrl =fxmlLoader.getController();
                    ctrl.setService(service);

                    Stage stageCreateAccount = new Stage();
                    Scene scene = new Scene(root, 1000, 500);

                    stageCreateAccount.setTitle("Create Account");
                    stageCreateAccount.setScene(scene);
                    stageCreateAccount.show();



                }catch(Exception ex){
                    System.out.println(ex);
                }
            }
        });
    }
    private Utilizator loginUserbyEmailAndPassword(String email,String password){
        Account accUser = null;
        try{
            accUser = service.findByEmailAndPassword(email,password);
            user_app = service.findOneUser(accUser.getId());
        }catch(Exception e){
            user_app = null;
        }

        return user_app;
    }
    public Utilizator getUtilizatorAplicatie(){
        return user_app;
    }
}
