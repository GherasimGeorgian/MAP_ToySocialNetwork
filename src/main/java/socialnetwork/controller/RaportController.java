package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

public class RaportController {

    private UserServiceFullDB service;
    private Utilizator user_app = null;

    @FXML
    private Button btn_activitate;

    //activitate Window
    Stage stageActivitate = new Stage();
    ActivitatiiUser activitatiiUserController;
    //

    //mesaje raport Window
    Stage stageMesajeRaport = new Stage();
    RaportMesajeController raportMesajeController;
    //

    @FXML
    private Button btn_mesaje;

    private Utilizator userApp;
    public void setService(UserServiceFullDB service,Utilizator userApp) {
        this.service=service;
        this.userApp = userApp;
    }


    @FXML
    public void initialize() {
        btn_activitate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/activitatiiuser.fxml"));
                    AnchorPane root = fxmlLoader.load();

                    activitatiiUserController = fxmlLoader.getController();
                    activitatiiUserController.setService(service,userApp);

                    Scene scene = new Scene(root, 630, 400);

                    stageActivitate.setTitle("Activitatii User");
                    stageActivitate.setScene(scene);
                    stageActivitate.show();
                }catch(Exception ex){
                    System.out.println(ex);
                }
            }
        });


        btn_mesaje.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/raportmesaje.fxml"));
                    AnchorPane root = fxmlLoader.load();

                    raportMesajeController = fxmlLoader.getController();
                    raportMesajeController.setService(service,userApp);

                    Scene scene = new Scene(root, 630, 400);

                    stageMesajeRaport.setTitle("Raport mesaje");
                    stageMesajeRaport.setScene(scene);
                    stageMesajeRaport.show();
                }catch(Exception ex){
                    System.out.println(ex);
                }
            }
        });
    }

}
