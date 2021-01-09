package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import socialnetwork.domain.Account;
import socialnetwork.domain.Eveniment;
import socialnetwork.domain.Invite;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListViewNotificari {





    static class Cell extends ListCell<String>{
        HBox hbox = new HBox();
        Button btn = new Button("Vezi mai mult");
        Label label = new Label();
        Pane pane = new Pane();

        public UserServiceFullDB service;
        public Utilizator user;


        ImageView imageView = new ImageView();

        Long idtype = new Long(-1);
        String type = new String();
        String list_element = new String();
        public Cell(UserServiceFullDB service,Utilizator user_app){
            super();
            this.service = service;
            this.user =user_app;

            hbox.getChildren().addAll(imageView,label,pane,btn);
            hbox.setHgrow(pane, Priority.ALWAYS);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(type.toLowerCase().equals("eveniment") && idtype != null){
                        Eveniment eventGasit =  service.findOneEvent(idtype);
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Informatii despre eveniment","Evenimentul "+eventGasit.getNameEvent() + " se organizeaza la data " + eventGasit.getDataString());
                    }
                    if(type.toLowerCase().equals("prietenie") && idtype != null){
                        Utilizator userGasit =  service.findOneUser(idtype);
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Informatii despre noua prietenie","Utilizatorul cu numele " + userGasit.getLastName() +" si prenumele " +  userGasit.getFirstName()+" te-a adaugat la prieteni");
                    }
                    if(type.toLowerCase().equals("invite") && idtype != null){
                        Invite invitatie =  service.findOneInvite(idtype);
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Informatii despre noua invitatie","Utilizatorul cu numele " + invitatie.getFromInvite().getLastName() +" si prenumele " +  invitatie.getFromInvite().getFirstName()+" ti-a trimis o cerere de prietenie!");
                    }
                    if(type.toLowerCase().equals("inviterej") && idtype != null){
                        Invite invitatie =  service.findOneInvite(idtype);
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Informatii despre invitatia respinsa","Utilizatorul cu numele " + invitatie.getFromInvite().getLastName() +" si prenumele " +  invitatie.getFromInvite().getFirstName()+" ti-a respins cererea de prietenie!");
                    }
                    if(type.toLowerCase().equals("prieteniedel") && idtype != null){
                        Utilizator userGasit =  service.findOneUser(idtype);
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Informatii despre prietenie","Utilizatorul cu numele " + userGasit.getLastName() +" si prenumele " +  userGasit.getFirstName()+" te-a sters de la prieteni");
                    }


                    if(type.toLowerCase().equals("mesaj") && idtype != null){
                        //typeid este acum id-ul userului de la care am primit mesaj
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/views/message.fxml"));
                            AnchorPane root=fxmlLoader.load();
                            MessageController messageController = fxmlLoader.getController();
                            Utilizator user_comunicate = service.findOneUser(idtype);
                            messageController.setService(service,user_app,user_comunicate);
                            Scene scene = new Scene(root, 400, 400);


                            Stage newStage = new Stage();


                            newStage.setTitle(user_comunicate.getId().toString());
                            newStage.setScene(scene);

                            newStage.show();

                        } catch (IOException e) {
                            Logger logger = Logger.getLogger(getClass().getName());
                            logger.log(Level.SEVERE, "Failed to create new Window.", e);
                        }
                    }

                    //stergem notoficariile

                    getListView().getItems().remove(getItem());


                }
            });
        }

        public void updateItem(String name, boolean empty){
            super.updateItem(name,empty);
            setText(null);
            setGraphic(null);
            if(name!=null && !empty){
                String[] parts = name.split("X");
                String[] info = parts[0].split(":");
                type = info[1];
                idtype = Long.parseLong(info[3]);
                imageView.setFitHeight(120);
                imageView.setFitWidth(120);
                imageView.setPreserveRatio(true);


                if(type.toLowerCase().equals("prietenie")  || type.toLowerCase().equals("mesaj")|| type.toLowerCase().equals("prieteniedel") ) {
                    Account acc = service.getAccountByUserId(idtype);
                    File file1 = new File(acc.getUrl_photo());
                    try {
                        Image a = new Image(file1.toURI().toURL().toExternalForm());
                        imageView.setImage(a);
                    } catch (Exception ex) {

                    }
                }
                else{
                    if(type.toLowerCase().equals("invite") || type.toLowerCase().equals("inviterej")) {
                        Long idAux = Long.parseLong(info[4]);
                        Account acc = service.getAccountByUserId(idAux);
                        File file1 = new File(acc.getUrl_photo());
                        try {
                            Image a = new Image(file1.toURI().toURL().toExternalForm());
                            imageView.setImage(a);
                        } catch (Exception ex) {

                        }
                    }

                }
                list_element = parts[1];
                label.setText(parts[1]);
                setGraphic(hbox);
            }
        }
    }
}
