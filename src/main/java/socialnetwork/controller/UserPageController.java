package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

import java.io.IOException;

public class UserPageController {
    private UserServiceFullDB service;
    private Utilizator user_app=null;

    public void setService(UserServiceFullDB service,Utilizator connectedUser) {
        this.user_app = connectedUser;
        this.service=service;
        loadPage();
    }


    @FXML
    private AnchorPane acdinamic;
    @FXML
    public void initialize() {

    }
    public void home(ActionEvent actionEvent) {
        try{

            Stage stageMessages = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/page.fxml"));
            AnchorPane root = loader.load();

            PageController ctrl=loader.getController();
            ctrl.setService(service,user_app);

            acdinamic.getChildren().setAll(root);
        }catch (IOException ex){

        }
    }

    public void messages(ActionEvent actionEvent) {
        try{

            Stage stageMessages = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/Prietenie.fxml"));
            AnchorPane root = loader.load();

            PrietenieController ctrl=loader.getController();
            ctrl.setService(service);


            acdinamic.getChildren().setAll(root);
        }catch (IOException ex){

        }
    }
    public void friends(ActionEvent actionEvent) {
    }
    public void reports(ActionEvent actionEvent) {
        try{

            Stage stageLogin = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/reports.fxml"));
            AnchorPane root = loader.load();

            RaportController ctrl = loader.getController();
            ctrl.setService(service);


            acdinamic.getChildren().setAll(root);
        }catch (IOException ex){

        }
    }
    public void logout(ActionEvent actionEvent) {
        Stage stage = (Stage) acdinamic.getScene().getWindow();
        stage.close();
        try {

            Stage stageLogin = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/loginStart.fxml"));
            AnchorPane root = loader.load();


            LoginStartController ctrl = loader.getController();
            ctrl.setService(service);
            stageLogin.setScene(new Scene(root, 700, 500));
            stageLogin.setTitle("LoginPage");
            stageLogin.show();
        }catch(Exception ex){

        }
    }

    private void loadPage(){

        try{

            Stage stageMessages = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/page.fxml"));
            AnchorPane root = loader.load();

            PageController ctrl=loader.getController();
            ctrl.setService(service,user_app);

            acdinamic.getChildren().setAll(root);
        }catch (IOException ex){

        }
    }

}
