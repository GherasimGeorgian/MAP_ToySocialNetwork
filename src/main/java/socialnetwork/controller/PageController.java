package socialnetwork.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import socialnetwork.domain.*;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PageController implements Observer<ChangeEvent> {

    private UserServiceFullDB service;
    private Utilizator user_app;
    private Page pageUser;

    //create abonamentele mele Window
    Stage stageAbonamenteleMele = new Stage();
    AbonamenteleMeleController abonamenteleMeleController;
    //


    //create event Window
    Stage stageCreateEvent = new Stage();
    CreateEventController createEventController;
    //

    //subscribe event Window
    Stage stageAbonareEvent = new Stage();
    AbonareEventController abonareEventController;
    //


    //addfriend Window
    Stage stageAddFriend = new Stage();
    AddFriendController addFriendController;
    //


    //friendrequests Window
    Stage stagefriendrequests = new Stage();
    FriendRequestsController friendrequestsController;
    //

    //message Window
    LinkedHashSet<Stage> ferestreMessage = new LinkedHashSet<>();
    MessageController messageController;
    //


    ObservableList<PrietenieDTO> modelCurrentUser = FXCollections.observableArrayList();



    Timer timer = new Timer();
    int secondsPassed = 0;

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            List<Eveniment> evenimente = service.evenimenteLaCareSuntAbonat(user_app);

            if(evenimente.size() > 0) {
                String eventsString= new String();
                for(Eveniment event : evenimente){
                    eventsString += event.toString() + '\n';
                }

                MessageAlert.showErrorMessage(null,"nnnnn");

            }
        }
    };

    //buttons


    @FXML
    Button btnAddNewFriend;

    @FXML
    Button btnfriendrequests;

    @FXML
    Button btnCreateEvent;

    @FXML
    Button btnAbonareEveniment;




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


    @FXML
    TextField txtSeconds;

    int secundeNotify = 15;

    public void setService(UserServiceFullDB Userservice,Utilizator userConn) {
        this.service=Userservice;
        this.user_app = userConn;
        pageUser = new Page(service);
        pageUser.createPage(userConn);
        service.addObserver(this);
        setCurrentUser();
        setFriendsSelectedUser();
        setlblFirstNameText(pageUser.getFirstName());
        setlblLastNameText(pageUser.getLastName());
        eventsAlert();

    }

    public void eventsAlert() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1*secundeNotify), ev -> {
            List<Eveniment> evenimente = service.evenimenteLaCareSuntAbonat(user_app);
            System.out.println(secundeNotify);

            if(evenimente.size() > 0) {
                String eventsString= new String();
                for(Eveniment event : evenimente){
                    eventsString += event.toString() + '\n';
                }

                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Urmeaza niste evenimente importante!","Evenimente la care esti abonat si urmeaza: " + '\n' +eventsString);

            }

        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void initModel() {
        modelCurrentUser.setAll(pageUser.getPrieteni());

    }

    @Override
    public void update(ChangeEvent messageTaskChangeEvent) {
        initModel();
    }


    @FXML
    public void initialize() {

       txtSeconds.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    if(!txtSeconds.getText().isEmpty()){

                        try{
                            secundeNotify = Integer.parseInt(txtSeconds.getText());
                        }catch(Exception ex){
                            MessageAlert.showErrorMessage(null,"Ai introdus ceva gresit");
                        }
                    }
                    else
                    {
                        MessageAlert.showErrorMessage(null,"Introdu ceva!");
                    }
                }
            }
        });

        for(int i=0;i<5;i++){
            Stage newStage = new Stage();
            ferestreMessage.add(newStage);
        }

        tableViewUserFriends.setRowFactory( tv -> {
            TableRow<PrietenieDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    PrietenieDTO rowData = row.getItem();

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText(rowData.getFirstName());
                    //alert.showAndWait();


                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/views/message.fxml"));
                        AnchorPane root=fxmlLoader.load();
                        messageController = fxmlLoader.getController();
                        Utilizator user_comunicate = service.findByNumePrenume(row.getItem().getFirstName(),row.getItem().getLastName());
                        messageController.setService(service,user_app,user_comunicate);
                        Scene scene = new Scene(root, 400, 400);

                        if(ferestreMessage.size() == 5){
                            ferestreMessage.clear();
                            for(int i=0;i<5;i++){
                                Stage newStage = new Stage();
                                ferestreMessage.add(newStage);
                            }
                        }

                        for(Stage st: ferestreMessage) {
                            if(st.getScene() == null){
                                st.setTitle(user_comunicate.getId().toString());
                                st.setScene(scene);
                            }
                        }

                        for(Stage st: ferestreMessage){
                            if(st.getScene()!=null && st.getTitle().equals(user_comunicate.getId().toString()) && !st.isShowing()) {
                                st.show();
                            }
                        }

                    } catch (IOException e) {
                        Logger logger = Logger.getLogger(getClass().getName());
                        logger.log(Level.SEVERE, "Failed to create new Window.", e);
                    }
                }
            });
            return row ;
        });


        //multiple selects
        tableViewUserFriends.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );


        tableViewUserFriends.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)) {
                    ObservableList<PrietenieDTO> selectedItems = tableViewUserFriends.getSelectionModel().getSelectedItems();
                    if (selectedItems.size() > 1) {

                        ArrayList<Utilizator> usersTo = new ArrayList<Utilizator>();
                        for (PrietenieDTO row : selectedItems) {
                            usersTo.add(service.findByNumePrenume(row.getFirstName(), row.getLastName()));
                        }

                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader();
                            fxmlLoader.setLocation(getClass().getResource("/views/message.fxml"));
                            AnchorPane root=fxmlLoader.load();
                            messageController = fxmlLoader.getController();
                            messageController.setServiceMultipleUsers(service,user_app,usersTo);
                            Scene scene = new Scene(root, 400, 400);

                            String idsUsers = new String();
                            for(Utilizator user : usersTo){
                                idsUsers+= user.getId().toString();
                            }

                            if(ferestreMessage.size() == 5){
                                ferestreMessage.clear();
                                for(int i=0;i<5;i++){
                                    Stage newStage = new Stage();
                                    ferestreMessage.add(newStage);
                                }
                            }

                            for(Stage st: ferestreMessage) {
                                if(st.getScene() == null){

                                    st.setTitle(idsUsers);
                                    st.setScene(scene);
                                }
                            }

                            for(Stage st: ferestreMessage){
                                if(st.getScene()!=null && st.getTitle().equals(idsUsers) && !st.isShowing()) {
                                    st.show();
                                }
                            }

                        } catch (IOException e) {
                            Logger logger = Logger.getLogger(getClass().getName());
                            logger.log(Level.SEVERE, "Failed to create new Window.", e);
                        }


                    }
                    else{
                        MessageAlert.showErrorMessage(null,"Trebuie sa selectez mai mult de 2 useri!");
                    }
                }
            }
        });



        //setam butonul add friend false deoarece nu avem selectat nici un user
        btnAddNewFriend.setDisable(true);
        btnfriendrequests.setDisable(true);







        btnAddNewFriend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/addfriend.fxml"));
                    AnchorPane root=fxmlLoader.load();

                    addFriendController =fxmlLoader.getController();
                    addFriendController.setService(service,user_app);

                    /*
                     * if "fx:controller" is not set in fxml
                     * fxmlLoader.setController(NewWindowController);
                     */
                    Scene scene = new Scene(root, 300, 200);

                    stageAddFriend.setTitle("Add new friend");
                    stageAddFriend.setScene(scene);
                    stageAddFriend.show();



                } catch (IOException e) {
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "Failed to create new Window.", e);
                }
            }
        });




        btnCreateEvent.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/create_event.fxml"));
                    AnchorPane root=fxmlLoader.load();

                    createEventController =fxmlLoader.getController();
                    createEventController.setService(service,user_app);


                    Scene scene = new Scene(root, 500, 500);

                    stageCreateEvent.setTitle("Create Event");
                    stageCreateEvent.setScene(scene);
                    stageCreateEvent.show();



                } catch (IOException e) {
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "Failed to create new Window.", e);
                }
            }
        });


        btnAbonareEveniment.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/abonare_event.fxml"));
                    AnchorPane root=fxmlLoader.load();

                    abonareEventController =fxmlLoader.getController();
                    abonareEventController.setService(service,user_app);


                    Scene scene = new Scene(root, 700, 500);

                    stageAbonareEvent.setTitle("Abonare Event");
                    stageAbonareEvent.setScene(scene);
                    stageAbonareEvent.show();



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
                    friendrequestsController.setService(service,user_app);

                    /*
                     * if "fx:controller" is not set in fxml
                     * fxmlLoader.setController(NewWindowController);
                     */
                    Scene scene = new Scene(root, 1000, 500);

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
            user_app = user_app;
            btnAddNewFriend.setDisable(false);
            btnfriendrequests.setDisable(false);
        }catch (Exception ex){
            user_app = null;
            btnAddNewFriend.setDisable(true);
            btnfriendrequests.setDisable(true);
        }
    }

    private void setFriendsSelectedUser() {
        modelCurrentUser.setAll(pageUser.getPrieteni());

        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<PrietenieDTO, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<PrietenieDTO, String>("lastName"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<PrietenieDTO, LocalDateTime>("date"));

        tableViewUserFriends.setItems(modelCurrentUser);

    }

    public void handleDeletePrietenieDto(ActionEvent actionEvent) {
        PrietenieDTO selected = tableViewUserFriends.getSelectionModel().getSelectedItem();
        if(selected != null){
            try {
                Prietenie deleted = service.stergePrietenie(user_app.getFirstName(), user_app.getLastName(), selected.getFirstName(), selected.getLastName());
                if(deleted == null){

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Stergere");
                    alert.setHeaderText(null);
                    alert.setContentText("S-a sters cu succes!");
                    alert.showAndWait();

                }
                setFriendsSelectedUser();
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
