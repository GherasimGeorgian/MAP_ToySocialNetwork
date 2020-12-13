package socialnetwork.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import socialnetwork.domain.Message;
import socialnetwork.domain.PrietenieDTO;
import socialnetwork.domain.ReplyMessage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.localdatetimeformat.FormatLDT;
import socialnetwork.utils.observer.Observer;

import java.io.File;
import java.io.FileOutputStream;

public class RaportMesajeController implements Observer<ChangeEvent> {
    private UserServiceFullDB service;
    private Utilizator selectedUser1 = null;
    private Utilizator selectedUser2 = null;

    @FXML
    private DatePicker date1;
    @FXML
    private DatePicker date2;
    @FXML
    private ComboBox comboBox1;
    @FXML
    private ComboBox comboBox2;

    @FXML
    private Button btn_genereaza;

    @FXML
    private ListView listView1;

    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelFriendsUser = FXCollections.observableArrayList();
    ObservableList<Message> modelMessages = FXCollections.observableArrayList();


    public void setService(UserServiceFullDB service) {
        this.service=service;
        service.addObserver(this);
        initModelUser();
        comboBox1.setItems(modelUsers);

    }

    @FXML
    public void initialize() {
        date1.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(date2.getValue() != null) {
                if (comboBox1.getSelectionModel().getSelectedItem() != null &&comboBox2.getSelectionModel().getSelectedItem() != null) {
                    showRaport();
                }
            }
        });

        date2.valueProperty().addListener((ov, oldValue, newValue) -> {
            if(date1.getValue() != null){
                if (comboBox1.getSelectionModel().getSelectedItem() != null && comboBox2.getSelectionModel().getSelectedItem() != null) {
                    showRaport();
                }
            }
        });
        comboBox1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
               try{
                selectedUser1 = (Utilizator) comboBox1.getSelectionModel().getSelectedItem();
                modelFriendsUser.setAll(selectedUser1.getFriends());
                comboBox2.setItems(modelFriendsUser);
                if(date1.getValue() != null &&date2.getValue() != null && comboBox2.getSelectionModel().getSelectedItem() != null){
                    showRaport();
                }
              }catch(Exception ex){

              }
            }
        });

        comboBox2.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                try {
                    selectedUser2 = (Utilizator) comboBox2.getSelectionModel().getSelectedItem();
                    if (date1.getValue() != null && date2.getValue() != null && comboBox1.getSelectionModel().getSelectedItem() != null) {
                        showRaport();
                    }
                }catch(Exception ex){

                }
            }
        });

        btn_genereaza.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(selectedUser1 != null && selectedUser2 != null && date1.getValue() != null && date2.getValue() != null && listView1.getItems().size() > 0) {
                    createPdf(selectedUser1,selectedUser2, listView1.getItems());
                } else{
                    MessageAlert.showErrorMessage(null,"Trebuie sa te asiguri ca ai facut filtrarea!");
                }
            }
        });



    }

    private void initModelUser() {

        modelUsers.setAll(service.getAllUsersList());
    }


    private void initModelMessages(Utilizator user1,Utilizator user2) {

        modelMessages.setAll(service.mesajePrimiteFromOneUserInterval(selectedUser1.getFirstName(), selectedUser1.getLastName(),selectedUser2.getFirstName(), selectedUser2.getLastName(),date1.getValue(),date2.getValue()));
    }


    public void showRaport(){
            initModelMessages(selectedUser1,selectedUser2);
            setListView();
    }
    public void setListView(){
        listView1.getItems().clear();




        for(Message msg : modelMessages) {
            if (msg instanceof ReplyMessage) {
                ReplyMessage msgReply = (ReplyMessage)msg;
                listView1.getItems().add("ReplyMessage:" + msgReply.getMessage() + " la data " + FormatLDT.convert(msgReply.getReplyMessage().getDate()));

            } else {
                listView1.getItems().add(msg.getMessage() + " la data " + FormatLDT.convert(msg.getDate()));
            }
        }
    }

    @Override
    public void update(ChangeEvent event) {
        ChangeEventType t = event.getType();
        //daca se modifica un mesaj
        if(t == ChangeEventType.M_ADD || t == ChangeEventType.M_UPD || t == ChangeEventType.M_DEL){
            if(date1.getValue() != null && date2.getValue() != null && comboBox1.getSelectionModel().getSelectedItem()!= null && comboBox2.getSelectionModel().getSelectedItem() != null) {
                showRaport();
            }
        }
        if(t == ChangeEventType.U_ADD || t == ChangeEventType.U_DEL || t == ChangeEventType.U_UPD){
            listView1.getItems().clear();
                initModelUser();
                comboBox1.getItems().clear();
                comboBox1.setItems(modelUsers);
        }
        if(t == ChangeEventType.P_ADD || t== ChangeEventType.P_DEL || t == ChangeEventType.P_UPD){
            if(comboBox1.getSelectionModel().getSelectedItem()!= null) {
                listView1.getItems().clear();
                comboBox2.getItems().clear();
                selectedUser1 = (Utilizator) comboBox1.getSelectionModel().getSelectedItem();
                modelFriendsUser.setAll(service.findByNumePrenume(selectedUser1.getFirstName(), selectedUser1.getLastName()).getFriends());
                comboBox2.setItems(modelFriendsUser);
            }
        }

    }
    private void createPdf(Utilizator user1,Utilizator user2, ObservableList<String> lista){
        String FILE_NAME = "C:/Users/ghera/Desktop/chillyfacts.pdf";
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(new File(FILE_NAME)));
            document.open();
            Paragraph p = new Paragraph();
            p.add("Mesajele lui " + user1.getFirstName() + " " + user1.getLastName() + " primite de la " + user2.getFirstName() + " " + user2.getLastName());
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
