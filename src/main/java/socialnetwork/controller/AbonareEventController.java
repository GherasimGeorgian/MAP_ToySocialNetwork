package socialnetwork.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.*;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AbonareEventController implements Observer<ChangeEvent> {
    private UserServiceFullDB service;
    private Utilizator user_app;
    public void setService(UserServiceFullDB service, Utilizator user) {
        this.user_app = user;
        this.service=service;
        service.addObserver(this);
        setFriendsSelectedUser();
    }

    int currentPageEvents = 0;

    @FXML
    Button btnNext;

    @FXML
    Button btnPrev;

    @FXML
    Button btnAbonare;

    @FXML
    Button btnDezabonare;


    //tableUsers
    @FXML
    TableView<Eveniment> tableViewEvenimente;
    @FXML
    TableColumn<Eveniment, String> tableColumnEventName;
    @FXML
    TableColumn<Eveniment, String> tableColumnDateEvent;



    ObservableList<Eveniment> modelEveniment = FXCollections.observableArrayList();

    private void setFriendsSelectedUser() {
        modelEveniment.setAll(StreamSupport.stream(service.getEventsOnPage(currentPageEvents).spliterator(), false)
                .collect(Collectors.toList()));

        tableColumnEventName.setCellValueFactory(new PropertyValueFactory<Eveniment, String>("nameEvent"));
        //tableColumnDateEvent.setCellValueFactory(new PropertyValueFactory<Eveniment, LocalDateTime>("dataEvent"));
        tableColumnDateEvent.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getDataString()));
        tableViewEvenimente.setItems(modelEveniment);

    }
    private void initModel() {
        modelEveniment.setAll(StreamSupport.stream(service.getEventsOnPage(currentPageEvents).spliterator(), false)
                .collect(Collectors.toList()));
    }
    @Override
    public void update(ChangeEvent messageTaskChangeEvent) {
        initModel();
    }

    @FXML
    public void initialize() {

        tableViewEvenimente.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        btnNext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentPageEvents++;
                initModel();
                if(modelEveniment.size() == 0){
                    currentPageEvents--;
                    initModel();
                }
            }
        });
        btnPrev.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(currentPageEvents > 0) {
                    currentPageEvents--;
                    initModel();
                }
            }
        });
        btnAbonare.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Eveniment selectedItem = tableViewEvenimente.getSelectionModel().getSelectedItem();
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
                Eveniment selectedItem = tableViewEvenimente.getSelectionModel().getSelectedItem();
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
    }
}
