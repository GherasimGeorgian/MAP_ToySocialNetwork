package socialnetwork.controller;



import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import jdk.jshell.execution.Util;
import socialnetwork.domain.*;

import socialnetwork.service.ServiceException;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.localdatetimeformat.FormatLDT;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;


public class ActivitatiiUser implements Observer<ChangeEvent> {
    private UserServiceFullDB service;
    private Utilizator selectedUser = null;
    @FXML
    ComboBox comboBoxUsers;

    @FXML
    DatePicker date1;

    @FXML
    DatePicker date2;


    @FXML
    ListView listRaport;


    @FXML
    Button btn_pdf;


    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    ObservableList<PrietenieDTO> modelPrietenii = FXCollections.observableArrayList();
    ObservableList<Message> modelMesaje = FXCollections.observableArrayList();

    public void setService(UserServiceFullDB service) {
        this.service=service;
        service.addObserver(this);
        initModelUser();
        comboBoxUsers.setItems(modelUsers);
    }


    private void initModelUser() {

        modelUsers.setAll(service.getAllUsersList());
    }
    private void initModelPrietenie(Utilizator user) {

        modelPrietenii.setAll(service.filtreazaPrieteniiInterval(user.getFirstName(), user.getLastName(),date1.getValue(),date2.getValue()));

    }

    private void initModelMessages(Utilizator user) {

        modelMesaje.setAll(service.mesajePrimiteDeUnUserInterval(user.getFirstName(), user.getLastName(),date1.getValue(),date2.getValue()));
    }
    @Override
    public void update(ChangeEvent event) {

            ChangeEventType t = event.getType();
            if (t == ChangeEventType.U_ADD || t == ChangeEventType.U_DEL || t == ChangeEventType.U_UPD) {
                initModelUser();
            }
        if(selectedUser != null) {

            if (t == ChangeEventType.P_ADD || t == ChangeEventType.P_DEL || t == ChangeEventType.P_UPD) {
                initModelPrietenie(selectedUser);
            }

            if (t == ChangeEventType.M_ADD || t == ChangeEventType.M_DEL || t == ChangeEventType.M_UPD) {
                initModelMessages(selectedUser);
            }
            setListView();
        }
    }

    @FXML
    public void initialize() {
        date1.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(date2.getValue() != null) {
                if (comboBoxUsers.getSelectionModel().getSelectedItem() != null) {
                    selectedUser = (Utilizator) comboBoxUsers.getSelectionModel().getSelectedItem();
                    showRaport();
                }
            }
        });

        date2.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(date1.getValue() != null){
                if (comboBoxUsers.getSelectionModel().getSelectedItem() != null) {
                    selectedUser = (Utilizator) comboBoxUsers.getSelectionModel().getSelectedItem();
                    showRaport();
                }
            }
        });
        comboBoxUsers.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if(date1.getValue() != null && date2.getValue() != null){
                    selectedUser = (Utilizator) comboBoxUsers.getSelectionModel().getSelectedItem();
                    showRaport();
                }
            }
        });

        btn_pdf.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if(selectedUser != null && date1.getValue() != null && date2.getValue() != null && listRaport.getItems().size() > 0) {
                    createPdf( selectedUser, listRaport.getItems());
                } else{
                    MessageAlert.showErrorMessage(null,"Trebuie sa te asiguri ca ai facut filtrarea!");
                }
            }
        });

    }
    public void setListView(){
        System.out.println(selectedUser.getFirstName());

        listRaport.getItems().clear();


        //listRaport.getItems().addAll(modelPrietenii);
        for(PrietenieDTO ptr : modelPrietenii){
            listRaport.getItems().add("S-a imprietenit cu " + ptr.getFirstName() + " " + ptr.getLastName() + " la data " + FormatLDT.convert(ptr.getDate()));
        }


       //listRaport.getItems().addAll(modelMesaje);
        for(Message msg : modelMesaje) {
            if (msg instanceof ReplyMessage) {
                ReplyMessage msgReply = (ReplyMessage)msg;
                listRaport.getItems().add("A primit un reply message: " + msgReply.getMessage() + " de la " + msgReply.getReplyMessage().getFrom().getFirstName() + " " + msgReply.getReplyMessage().getFrom().getLastName() + " : " + msgReply.getReplyMessage().getMessage() + " la data " + FormatLDT.convert(msgReply.getDate()));

            } else {
                listRaport.getItems().add("A primit un mesaj de la: " + msg.getFrom().getFirstName() + " " + msg.getFrom().getLastName() + " la data " + FormatLDT.convert(msg.getDate()) + " : " + msg.getMessage());

            }
        }
    }
    public void showRaport(){
        Utilizator user = (Utilizator)comboBoxUsers.getSelectionModel().getSelectedItem();
        if(user != null){
            selectedUser=user;
            initModelPrietenie(selectedUser);
            initModelMessages(selectedUser);
            setListView();
        }

    }



    private void createPdf(Utilizator user, ObservableList<String> lista){
        String FILE_NAME = "C:/Users/ghera/Desktop/chillyfacts.pdf";
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(new File(FILE_NAME)));
            document.open();
            Paragraph p = new Paragraph();
            p.add("Activitatiile lui " + user.getFirstName() + " " + user.getLastName());
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            for(String s : lista) {
                Paragraph p2 = new Paragraph();
                p2.add(s);
                document.add(p2);
            }
//            Font f = new Font();
//            f.setStyle(Font.BOLD);
//            f.setSize(8);
            document.add(Image.getInstance("C:/Users/ghera/Desktop/efb5d90eeb07bb5914f094007da4c2f7.png"));
            document.close();
            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
