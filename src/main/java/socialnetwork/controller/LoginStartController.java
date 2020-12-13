package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
    TextField textFieldFirstName;
    @FXML
    TextField textFieldLastName;


    //buttons
    @FXML
    Button btnLogin;

    @FXML
    Button btnCancel;
    //cava
    int ceva;
    @FXML
    public void initialize() {



        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(textFieldFirstName.getText().isEmpty() || textFieldLastName.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Numele si prenumele nu trebuie sa fie nule!");
                    alert.showAndWait();
                }
                else{
                    user_app = loginUserbyLastFirstName(textFieldFirstName.getText(),textFieldLastName.getText());
                    if(user_app == null){
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText(null);
                        alert.setContentText("Userul introdus nu exista!");
                        alert.showAndWait();
                        textFieldFirstName.clear();
                        textFieldLastName.clear();
                    }
                    else{
                        try {

                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/views/userpage.fxml"));
                            AnchorPane root=fxmlLoader.load();

                            UserPageController ctrl =fxmlLoader.getController();
                            ctrl.setService(service);

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
    }
    private Utilizator loginUserbyLastFirstName(String firstName,String lastName){

        try{
            user_app = service.findByNumePrenume(firstName,lastName);
        }catch(Exception e){
            user_app = null;
        }
        return user_app;
    }
    public Utilizator getUtilizatorAplicatie(){
        return user_app;
    }
}
