package socialnetwork.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import socialnetwork.domain.*;
import socialnetwork.service.ServiceException;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.localdatetimeformat.FormatLDT;
import socialnetwork.utils.observer.Observer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PageController implements Observer<ChangeEvent> {

    private UserServiceFullDB service;
    private Utilizator user_app;
    private Page pageUser;
    private List<Utilizator> notFriends;
    private Utilizator selected_user_add = null;
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

            if (evenimente.size() > 0) {
                String eventsString = new String();
                for (Eveniment event : evenimente) {
                    eventsString += event.toString() + '\n';
                }

                MessageAlert.showErrorMessage(null, "nnnnn");

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

    Timeline timeline;


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

    int secundeNotify = 60;

    ObservableList<Invite> modelInvite = FXCollections.observableArrayList();

    @FXML
    TableView<Invite> tableViewInvite2;
    @FXML
    TableColumn<Invite, String> tableColumnFirstNameToRec2;

    @FXML
    TableColumn<Invite, String> tableColumnLastNameToRec2;


    //textfields
    @FXML
    TextField textFieldSearchFriend;

    //labels
    @FXML
    Label lblUser;


    //buttons
    @FXML
    Button btnSendInvite;


    @FXML
    ListView listViewNotificari;

    ObservableList<String> listViewnotificari = FXCollections.observableArrayList();

    @FXML
    TableView<Eveniment> tableViewEvents;

    @FXML
    Button btnNext;

    @FXML
    Button btnPrev;

    @FXML
    Button btnAbonare;

    @FXML
    Button btnDezabonare;


    @FXML
    Button btnRejectInvite;

    @FXML
    TableColumn<Eveniment, String> tableColumnEventName2;
    @FXML
    TableColumn<Eveniment, String> tableColumnDateEvent2;

    @FXML
    ImageView imageViewProfile;

    ObservableList<Eveniment> modelEveniment = FXCollections.observableArrayList();


    public void setService(UserServiceFullDB Userservice, Utilizator userConn) {
        this.service = Userservice;
        this.user_app = userConn;
        pageUser = new Page(service);
        pageUser.createPage(userConn);
        service.addObserver(this);
        setCurrentUser();
        setFriendsSelectedUser();
        initModelFriendsRequests();
        setModelEvents();
        setImageProfile(user_app);
        notFriends = service.notRelatiiUser(user_app.getFirstName(), user_app.getLastName());
        setlblFirstNameText(pageUser.getFirstName());
        setlblLastNameText(pageUser.getLastName());
        AlertaNotificare();
        eventsAlertStart();

    }

    public void AlertaNotificare() {
        List<Eveniment> evenimente = service.evenimenteLaCareSuntAbonat(user_app);
        System.out.println(secundeNotify);

        if (evenimente.size() > 0) {
            String eventsString = new String();
            for (Eveniment event : evenimente) {
                eventsString += event.toString() + '\n';
            }

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Urmeaza niste evenimente importante!", "Evenimente la care esti abonat si urmeaza: " + '\n' + eventsString);

        }
    }


    public void eventsAlertStart() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1 * secundeNotify), ev -> {
            AlertaNotificare();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void eventsAlertStop() {
        timeline.stop();
    }

    private void initModel() {
        modelCurrentUser.setAll(pageUser.getPrieteni());

    }


    @Override
    public void update(ChangeEvent event) {

        ChangeEventType t = event.getType();
        if (t == ChangeEventType.I_ADD || t == ChangeEventType.I_DEL || t == ChangeEventType.I_UPD ||
                t == ChangeEventType.P_ADD || t == ChangeEventType.P_DEL || t == ChangeEventType.P_UPD) {
            initModel();
            initModelFriendsRequests();
            notFriends = service.notRelatiiUser(user_app.getFirstName(), user_app.getLastName());
        }
        if (t == ChangeEventType.P_ADD) {
            Prietenie prietenie = (Prietenie) event.getData();
            if (prietenie.getId().getRight().toString().equals(user_app.getId().toString())) {
                listViewNotificari.getItems().add(new String("TYPE:PRIETENIE:" + "ID:" + prietenie.getId().getLeft() + ":X" + "Cineva te-a adaugat la prieteni!"));
            }
        }
        if (t == ChangeEventType.M_ADD) {
            Message msg = (Message) event.getData();
            if (msg.getTo().contains(user_app)) {
                listViewNotificari.getItems().add(new String("TYPE:MESAJ:" + "ID:" + msg.getFrom().getId() + ":X" + "Ai primit un mesaj de la " + msg.getFrom().getFirstName() + " " + msg.getFrom().getLastName() + "!"));
            }
        }
        if (t == ChangeEventType.E_ADD) {
            initModelEvents();
            Eveniment evenimentAdaugat = (Eveniment) event.getData();
            listViewNotificari.getItems().add(new String("TYPE:EVENIMENT:" + "ID:" + evenimentAdaugat.getId() + ":X" + "A fost recent un eveniment! " + evenimentAdaugat.getNameEvent() + " la data " + FormatLDT.convert(evenimentAdaugat.getDataEvent())));

        }

        if (t == ChangeEventType.I_ADD) {
            Invite invitatie = (Invite) event.getData();
            if (invitatie.getToInvite().getId().toString().equals(user_app.getId().toString())) {
                listViewNotificari.getItems().add(new String("TYPE:INVITE:" + "ID:" + invitatie.getId() + ":"+invitatie.getFromInvite().getId()+":X" + "Cineva ti-a trimis o cerere de prietenie!"));
            }

        }

        if(t == ChangeEventType.I_UPD){
            Invite invitatie = (Invite)event.getData();

            if(invitatie.getFromInvite().getId().toString().equals(user_app.getId().toString())){
                listViewNotificari.getItems().add(new String("TYPE:INVITEREJ:"+"ID:"+invitatie.getId()+ ":"+invitatie.getToInvite().getId()+":X"+ "Cineva ti-a respins cererea de prietenie!"));
            }
        }
        if (t == ChangeEventType.P_DEL) {
            Prietenie prietenie = (Prietenie) event.getData();
            Utilizator user = (Utilizator) event.getOldData();
            if (!(user.getId().toString().equals(user_app.getId().toString()))) {
                listViewNotificari.getItems().add(new String("TYPE:PRIETENIEDEL:" + "ID:" + user.getId() + ":X" + "Cineva te-a eliminat de la prieteni!"));
            }

        }

    }

    private void initModelFriendsRequests() {
        Iterable<Invite> invites = service.allinvitationsToUsers(user_app.getFirstName(),user_app.getLastName());
        List<Invite> invitesList = StreamSupport.stream(invites.spliterator(),false)
                .collect(Collectors.toList());
        modelInvite.setAll(invitesList);
    }
    int currentPageEvents = 0;

    private void setModelEvents() {
        modelEveniment.setAll(StreamSupport.stream(service.getEventsOnPage(currentPageEvents).spliterator(), false)
                .collect(Collectors.toList()));

        tableColumnEventName2.setCellValueFactory(new PropertyValueFactory<Eveniment, String>("nameEvent"));
        tableColumnDateEvent2.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getDataString()));

        tableViewEvents.setItems(modelEveniment);

    }
    private void initModelEvents() {

        modelEveniment.setAll(StreamSupport.stream(service.getEventsOnPage(currentPageEvents).spliterator(), false)
                .collect(Collectors.toList()));
    }

    @FXML
    public void initialize() {


        listViewNotificari.setCellFactory(param -> new ListViewNotificari.Cell(service,user_app));
        textFieldSearchFriend.textProperty().addListener(x->handleFilterFriends());

        tableViewEvents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        btnNext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentPageEvents++;
                initModelEvents();
                if(modelEveniment.size() == 0){
                    currentPageEvents--;
                    initModelEvents();
                }
            }
        });
        btnPrev.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(currentPageEvents > 0) {
                    currentPageEvents--;
                    initModelEvents();
                }
            }
        });
        btnAbonare.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Eveniment selectedItem = tableViewEvents.getSelectionModel().getSelectedItem();
                if(selectedItem == null){
                    MessageAlert.showErrorMessage(null,"Trebuie sa selectezi un eveniment pentru a te abona la acesta!!");
                }
                else{
                    AbonareEveniment abonare =  service.abonareEveniment(selectedItem,user_app);
                    if(abonare == null){
                        MessageAlert.showErrorMessage(null,"Esti deja abonat la acest eveniment!");
                    }
                    else{
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Succes","Te-ai abonat cu succes la acest eveniment!");
                    }
                }
            }
        });
        btnDezabonare.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Eveniment selectedItem = tableViewEvents.getSelectionModel().getSelectedItem();
                if(selectedItem == null){
                    MessageAlert.showErrorMessage(null,"Trebuie sa selectezi un eveniment pentru a te dezabona de la acesta!!");
                }
                else{
                    AbonareEveniment abonare =  service.dezabonareEveniment(selectedItem,user_app);
                    if(abonare == null){
                        MessageAlert.showErrorMessage(null,"S-a produs o eroare!");
                    }
                    else{
                        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Succes","Te-ai dezabonat cu succes la acest eveniment!");
                    }
                }
            }
        });

        btnSendInvite.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(selected_user_add != null) {
                    //verificam daca exista deja o invitatie
                    Invite inv = service.findInvitebytwoUsers(user_app.getFirstName(), user_app.getLastName(),selected_user_add.getFirstName(),selected_user_add.getLastName());
                    if (inv!= null && inv.getStatus() != InviteStatus.APPROVED) {
                        setlblUser("");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Atentie");
                        alert.setHeaderText(null);
                        alert.setContentText("Exista deja o invitatie catre userul: " + selected_user_add.getFirstName() + " " + selected_user_add.getLastName());
                        alert.showAndWait();

                    }
                    // altfel cream o noua invitatie
                    else{
                        try {
                            service.trimiteInvitatie(user_app.getFirstName(), user_app.getLastName(), selected_user_add.getFirstName(), selected_user_add.getLastName());
                            setlblUser("");
                        }catch (ServiceException ex){
                            setlblUser("");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Atentie");
                            alert.setHeaderText(null);
                            alert.setContentText(ex.getMessage());
                            alert.showAndWait();

                        }
                    }
                }
                else{
                    setlblUser("");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Atentie");
                    alert.setHeaderText(null);
                    alert.setContentText("Nu s-a ales un user caruia sa i se trimita invitatia!");
                    alert.showAndWait();
                }
            }
        });
        //

        tableViewInvite2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableColumnFirstNameToRec2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFromInvite().getFirstName()));
        tableColumnLastNameToRec2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFromInvite().getLastName()));
        tableViewInvite2.setItems(modelInvite);


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
                            if(secundeNotify == -1) {
                                timeline.stop();
                                MessageAlert.showErrorMessage(null,"Ai oprit notificarile");
                            }
                            else {
                                if(secundeNotify > 5) {
                                    eventsAlertStop();
                                    eventsAlertStart();
                                }
                            }
                            if(secundeNotify<1) {
                                eventsAlertStop();
                                MessageAlert.showErrorMessage(null,"Seteaza mai mult de 10 secunde pentru notificari"); }
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
        btnRejectInvite.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Invite selected = tableViewInvite2.getSelectionModel().getSelectedItem();
                if(selected != null) {
                    if(selected.getStatus() == InviteStatus.PENDING) {
                        try {
                            service.rejectInvite(selected);
                        } catch (Exception e) {

                        }

                    }
                    else{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Atentie");
                        alert.setHeaderText(null);
                        alert.setContentText("Invitatia trebuie sa fie Pending!");
                        alert.showAndWait();
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
        imageViewProfile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                changeProfilePhoto(user_app);
                event.consume();
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
    private void handleFilterFriends() {

        Predicate<Utilizator> numePredicate = x->x.getLastName().startsWith(textFieldSearchFriend.getText());

        try {
            Utilizator user_gasit = StreamSupport.stream(notFriends.spliterator(), false)
                    .filter(numePredicate)
                    .collect(Collectors.toList()).get(0);
            setlblUser(user_gasit.getFirstName() + " " + user_gasit.getLastName());
            selected_user_add = user_gasit;
        }catch(Exception ex){
            setlblUser("Nimic gasit");
        }
//        modelGrade.setAll(getNotaDTOList()
//                .stream()
//                .filter(numePredicate.and(temaPredicate).and(notaPredicate))
//                .collect(Collectors.toList())
//        );
    }
    private void setlblUser(String text)
    {
        lblUser.setText(text);
    }

    private void setlblFirstNameText(String text)
    {
        lblFirstName.setText(text);
    }

    private void setlblLastNameText(String text)
    {
        lblLastName.setText(text);
    }

    public void handleAcceptInvite(ActionEvent actionEvent) {
        Invite selected = tableViewInvite2.getSelectionModel().getSelectedItem();
        if(selected != null) {
            if(selected.getStatus() == InviteStatus.PENDING) {
                try {
                    service.acceptaInvitatie(selected.getId());
                } catch (Exception e) {

                }

            }
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Atentie");
                alert.setHeaderText(null);
                alert.setContentText("Invitatia trebuie sa fie Pending!");
                alert.showAndWait();
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
    final FileChooser fileChooser = new FileChooser();

    public void changeProfilePhoto(Utilizator user) {
        Account account = service.findAccountByUserName(user);
        Stage stage = (Stage) btnAbonare.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Account user_account = service.findAccountByUserName(user);
            user_account.setUrl_photo(file.getAbsolutePath());
            service.updateAccount(user_account);
            setImageProfile(user);
        }
    }


    public void setImageProfile(Utilizator user){
       try{
        Account account = service.findAccountByUserName(user);
        String url_image_profile = account.getUrl_photo();
        System.out.println(url_image_profile);
        if(url_image_profile.equals("null")){
            File file1 = new File("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/profile.jpg");
            Image a = new Image(file1.toURI().toURL().toExternalForm());
            imageViewProfile.setImage(a);
        }else{
            File file1 = new File(account.getUrl_photo());
            Image a = new Image(file1.toURI().toURL().toExternalForm());
            imageViewProfile.setImage(a);
        }
      }catch(Exception ex){
           System.out.println(ex.getMessage());;
      }
    }
}
