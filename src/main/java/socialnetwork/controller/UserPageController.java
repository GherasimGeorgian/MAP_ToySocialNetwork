package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserPageController {
    private UserServiceFullDB service;
    private Utilizator user_app=null;
    private int darkMode;
    public void setService(UserServiceFullDB service,Utilizator connectedUser,int darkMode) {
        this.user_app = connectedUser;
        this.service=service;
        this.darkMode = darkMode;
        loadPage();
    }

    public void loadMode(){
        if(darkMode == 0){
            darkMode();
        }else{
            whiteMode();
        }
    }



    private List<AnchorPane> listAP = new ArrayList<>();
    private List<Button> listButtons = new ArrayList<>();
    @FXML
    Button btn1;
    @FXML
    Button btn2;
    @FXML
    Button btn3;
    @FXML
    Button btn4;
    @FXML
    Button btn5;
    @FXML
    Button btn6;
    @FXML
    Button btn7;
    @FXML
    Button btn8;


    @FXML
    VBox vBoxPage;

    @FXML
    AnchorPane acPaneLeft;

    @FXML
    private AnchorPane acdinamic;

    public void whiteMode(){
        //anchorPane
        acdinamic.setBackground(new Background(new BackgroundFill(Color.web("#" + "ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
        acPaneLeft.setBackground(new Background(new BackgroundFill(Color.web("#" + "ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));
        //vbox
        vBoxPage.setBackground(new Background(new BackgroundFill(Color.web("#" + "ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));

    }

    public void darkMode(){
        //anchorPane
        acdinamic.setBackground(new Background(new BackgroundFill(Color.web("#" + "000000"), CornerRadii.EMPTY, Insets.EMPTY)));
        acPaneLeft.setBackground(new Background(new BackgroundFill(Color.web("#" + "000000"), CornerRadii.EMPTY, Insets.EMPTY)));
        //vbox
        vBoxPage.setBackground(new Background(new BackgroundFill(Color.web("#" + "000000"), CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @FXML
    public void initialize() {

    }
    public void home(ActionEvent actionEvent) {
        try{


            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/page.fxml"));
            AnchorPane root = loader.load();
            listAP.add(root);
            PageController ctrl=loader.getController();
            ctrl.setService(service,user_app,darkMode);

            acdinamic.getChildren().setAll(root);
        }catch (IOException ex){

        }
    }

    public void messages(ActionEvent actionEvent) {
//        for(AnchorPane acPane: listAP){
//            acPane.setBackground(new Background(new BackgroundFill(Color.web("#" + "000000"), CornerRadii.EMPTY, Insets.EMPTY)));
//        }
//        int i=0;
//        for(Button btn : listButtons){
//            if(i%2==0)
//            btn.setBackground(new Background(new BackgroundFill(Color.web("#" + "000000"), CornerRadii.EMPTY, Insets.EMPTY)));
//            i++;
//        }
       // acdinamic.setBackground(new Background(new BackgroundFill(Color.web("#" + "FB5607"), CornerRadii.EMPTY, Insets.EMPTY)));
//        try{
//
//            Stage stageMessages = new Stage();
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/views/Prietenie.fxml"));
//            AnchorPane root = loader.load();
//
//            PrietenieController ctrl=loader.getController();
//            ctrl.setService(service);
//
//
//            acdinamic.getChildren().setAll(root);
//        }catch (IOException ex){
//
//        }
    }
    public void friends(ActionEvent actionEvent) {
    }
    public void reports(ActionEvent actionEvent) {
        try{


            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/reports.fxml"));
            AnchorPane root = loader.load();
            listAP.add(root);
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
            listAP.add(root);

            LoginStartController ctrl = loader.getController();
            ctrl.setService(service);
            stageLogin.setScene(new Scene(root, 700, 500));
            stageLogin.setTitle("LoginPage");
            stageLogin.show();
        }catch(Exception ex){

        }
    }

    private void loadPage(){
        listAP.add(acdinamic);
        listButtons.add(btn1);
        listButtons.add(btn2);
        listButtons.add(btn3);
        listButtons.add(btn4);
        listButtons.add(btn5);
        listButtons.add(btn6);
        listButtons.add(btn7);
        listButtons.add(btn8);
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/page.fxml"));
            AnchorPane root = loader.load();
            listAP.add(root);
            PageController ctrl=loader.getController();
            ctrl.setService(service,user_app,darkMode);

            acdinamic.getChildren().setAll(root);
        }catch (IOException ex){

        }
    }

}
