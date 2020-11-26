package socialnetwork.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jdk.jshell.execution.Util;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.InviteValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.PrietenieValidatorDb;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.RepositoryOptional;
import socialnetwork.repository.database.InviteDB;
import socialnetwork.repository.database.MessageDB;
import socialnetwork.repository.database.PrietenieDB;
import socialnetwork.repository.database.UtilizatorDB;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.events.PrietenieDTOChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PrietenieController  implements Observer<PrietenieDTOChangeEvent> {

    private UserServiceFullDB service;
    Utilizator current_user;
    private Utilizator user_app;

    //login Window
    Stage stageLogin = new Stage();
    LoginController loginController;
    //

    //addfriend Window
    Stage stageAddFriend = new Stage();
    AddFriendController addFriendController;
    //


    //friendrequests Window
    Stage stagefriendrequests = new Stage();
    FriendRequestsController friendrequestsController;
    //



    ObservableList<PrietenieDTO> modelCurrentUser = FXCollections.observableArrayList();


    //buttons

    @FXML
    Button btnsearchFriendsUser;

    @FXML
    Button btnLogin;


    @FXML
    Button btnAddNewFriend;

    @FXML
    Button btnfriendrequests;

    //textfields
    @FXML
    TextField textFieldFirstName;
    @FXML
    TextField textFieldLastName;


    //tableUsers
    @FXML
    TableView<PrietenieDTO> tableViewUserFriends;
    @FXML
    TableColumn<PrietenieDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<PrietenieDTO, String> tableColumnLastName;
    @FXML
    TableColumn<PrietenieDTO, LocalDateTime> tableColumnDate;

    //label

    @FXML
    Label lblFirstName;

    @FXML
    Label lblLastName;

    public void setService(UserServiceFullDB Userservice) {
        service=Userservice;
        service.addObserver(this);
     //   initModel();

    }
    private void initModel() {
        modelCurrentUser.setAll(service.relatiiUser(current_user.getFirstName(),current_user.getLastName()));

    }

    @Override
    public void update(PrietenieDTOChangeEvent messageTaskChangeEvent) {
        initModel();
    }

    @FXML
    public void initialize() {

        //setam butonul add friend false deoarece nu avem selectat nici un user
        btnAddNewFriend.setDisable(true);
        btnfriendrequests.setDisable(true);

        stageLogin.setOnHiding(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                user_app = loginController.getUtilizatorAplicatie();
                if(user_app == null){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Nu esti logat!");
                    alert.showAndWait();
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Te-ai logat cu succes!");
                    alert.showAndWait();
                    setlblFirstNameText(user_app.getFirstName());
                    setlblLastNameText(user_app.getLastName());
                    btnLogin.setDisable(true);
                }
            }
        });

        btnsearchFriendsUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                   setCurrentUser();
                   if(current_user != null) {
                       setFriendsSelectedUser();
                       btnAddNewFriend.setDisable(false);
                       btnfriendrequests.setDisable(false);
                   }
                   else{
                       btnAddNewFriend.setDisable(true);
                       btnfriendrequests.setDisable(true);
                       tableViewUserFriends.getItems().clear();
                       Alert alert = new Alert(Alert.AlertType.WARNING);
                       alert.setTitle("Warning");
                       alert.setHeaderText(null);
                       alert.setContentText("Userul introdus nu exista!");
                       alert.showAndWait();
                   }

            }
        });


        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/login.fxml"));
                    AnchorPane root=fxmlLoader.load();

                    loginController =fxmlLoader.getController();
                    loginController.setService(service);

                    /*
                     * if "fx:controller" is not set in fxml
                     * fxmlLoader.setController(NewWindowController);
                     */
                    Scene scene = new Scene(root, 630, 400);

                    stageLogin.setTitle("New Window");
                    stageLogin.setScene(scene);
                    stageLogin.show();



                } catch (IOException e) {
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "Failed to create new Window.", e);
                }
            }
        });
        btnAddNewFriend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/addfriend.fxml"));
                    AnchorPane root=fxmlLoader.load();

                    addFriendController =fxmlLoader.getController();
                    addFriendController.setService(service,current_user);

                    /*
                     * if "fx:controller" is not set in fxml
                     * fxmlLoader.setController(NewWindowController);
                     */
                    Scene scene = new Scene(root, 200, 100);

                    stageAddFriend.setTitle("Add new friend");
                    stageAddFriend.setScene(scene);
                    stageAddFriend.show();



                } catch (IOException e) {
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "Failed to create new Window.", e);
                }
            }
        });


        btnfriendrequests.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/friendrequests.fxml"));
                    AnchorPane root=fxmlLoader.load();

                    friendrequestsController =fxmlLoader.getController();
                    friendrequestsController.setService(service,current_user);

                    /*
                     * if "fx:controller" is not set in fxml
                     * fxmlLoader.setController(NewWindowController);
                     */
                    Scene scene = new Scene(root, 200, 100);

                    stagefriendrequests.setTitle("Friend requests");
                    stagefriendrequests.setScene(scene);
                    stagefriendrequests.show();



                } catch (IOException e) {
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "Failed to create new Window.", e);
                }
            }
        });

    }



    private List<Utilizator> getAllUsers() {
        return StreamSupport.stream(service.getAllUsers().spliterator(), false)
                .collect(Collectors.toList());
    }

    private void setCurrentUser() {
     try {
         Utilizator user = service.findByNumePrenume(textFieldFirstName.getText(), textFieldLastName.getText());
         current_user = user;
         btnAddNewFriend.setDisable(false);
         btnfriendrequests.setDisable(false);
      }catch (Exception ex){
          current_user = null;
         btnAddNewFriend.setDisable(true);
         btnfriendrequests.setDisable(true);
      }
    }

    private void setFriendsSelectedUser() {
        modelCurrentUser.setAll(service.relatiiUser(current_user.getFirstName(),current_user.getLastName()));

        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<PrietenieDTO, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<PrietenieDTO, String>("lastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<PrietenieDTO, LocalDateTime>("date"));

        tableViewUserFriends.setItems(modelCurrentUser);

    }

    public void handleDeletePrietenieDto(ActionEvent actionEvent) {
        PrietenieDTO selected = tableViewUserFriends.getSelectionModel().getSelectedItem();
        if(selected != null){
            try {
                Prietenie deleted = service.stergePrietenie(current_user.getFirstName(), current_user.getLastName(), selected.getFirstName(), selected.getLastName());
                if(deleted == null){

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Stergere");
                    alert.setHeaderText(null);
                    alert.setContentText("S-a sters cu succes!");
                    alert.showAndWait();

                }
            }catch(Exception e)
            {

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

    private void setlblFirstNameText(String text)
    {
        lblFirstName.setText(text);
    }

    private void setlblLastNameText(String text)
    {
        lblLastName.setText(text);
    }
}
