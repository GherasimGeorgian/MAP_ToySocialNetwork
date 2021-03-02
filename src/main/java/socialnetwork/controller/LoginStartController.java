package socialnetwork.controller;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import socialnetwork.domain.Account;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.password.PasswordHashing;
import socialnetwork.utils.threads.NewThreadWindow;

import javax.swing.event.ChangeListener;
import java.io.File;

public class LoginStartController {
    private UserServiceFullDB service;
    private Utilizator user_app=null;

    public void setService(UserServiceFullDB service) {
        this.service=service;
        setImageWhite();
    }
    //textfields
    @FXML
    TextField textFieldEmail;
    @FXML
    PasswordField textFieldPassword;


    //buttons
    @FXML
    Button btnLogin;

    @FXML
    Button btnCancel;


    @FXML
    Button btnCreateNewAccount;

    @FXML
    CheckBox cBDarkMode;

    @FXML
    AnchorPane acLogin;

    @FXML
    Label lblName;

    @FXML
    Label lblPass;

    @FXML
    ImageView ivback;

    int darkMode = 1;

    public void whiteMode(){

        setImageWhite();
        //anchorPane
        acLogin.setBackground(new Background(new BackgroundFill(Color.web("#" + "ffffff"), CornerRadii.EMPTY, Insets.EMPTY)));

        //labels
        lblName.setTextFill(Color.web("#000000"));
        lblPass.setTextFill(Color.web("#000000"));
        //checkbox
        cBDarkMode.setTextFill(Color.web("#000000"));

        //buttons
        //buttons

        btnLogin.setId("sale");
        btnCancel.setId("sale");
        btnCreateNewAccount.setId("sale");

    }

    public void darkMode(){
        setImageBlack();
        //anchorPane
        acLogin.setBackground(new Background(new BackgroundFill(Color.web("#" + "000000"), CornerRadii.EMPTY, Insets.EMPTY)));

        //labels
        lblName.setTextFill(Color.web("#ffffff"));
        lblPass.setTextFill(Color.web("#ffffff"));
        //checkbox
        cBDarkMode.setTextFill(Color.web("#ffffff"));

        //buttons

        btnLogin.setId("sale2");
        btnCancel.setId("sale2");
        btnCreateNewAccount.setId("sale2");
    }

    private void setImageWhite(){

        try{

            File file1 = new File("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/nice.jpg");
            Image a = new Image(file1.toURI().toURL().toExternalForm());
            ivback.setImage(a);

        }catch(Exception ex){
            System.out.println(ex.getMessage());;
        }
    }

    private void setImageBlack(){

        try{

            File file1 = new File("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/nice2.jpg");
            Image a = new Image(file1.toURI().toURL().toExternalForm());
            ivback.setImage(a);

        }catch(Exception ex){
            System.out.println(ex.getMessage());;
        }
    }

    @FXML
    public void initialize() {


        cBDarkMode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(cBDarkMode.isSelected()){
                    darkMode();
                    darkMode =0;
                }else{
                    whiteMode();
                    darkMode =1;
                }
            }
        });

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(textFieldEmail.getText().isEmpty() || textFieldPassword.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Emailul si parola nu trebuie sa fie nule!");
                    alert.showAndWait();
                }
                else{
                    user_app = loginUserbyEmailAndPassword(textFieldEmail.getText(), PasswordHashing.doHashing(textFieldPassword.getText()));
                    if(user_app == null){
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText(null);
                        alert.setContentText("Userul introdus nu exista!");
                        alert.showAndWait();
                        textFieldEmail.clear();
                        textFieldPassword.clear();
                    }
                    else{
                        NewThreadWindow th = new NewThreadWindow(service,user_app,darkMode);
                        th.execute();

                        textFieldEmail.clear();
                        textFieldPassword.clear();
                    }
                }
            }
        });

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) btnCancel.getScene().getWindow();
                stage.close();
            }
        });


        btnCreateNewAccount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/create_account.fxml"));
                    AnchorPane root=fxmlLoader.load();

                    CreateAccountController ctrl =fxmlLoader.getController();
                    ctrl.setService(service,darkMode);

                    Stage stageCreateAccount = new Stage();
                    Scene scene = new Scene(root, 1000, 500);

                    stageCreateAccount.setTitle("Create Account");
                    stageCreateAccount.setScene(scene);
                    stageCreateAccount.show();



                }catch(Exception ex){
                    System.out.println(ex);
                }
            }
        });
    }
    private Utilizator loginUserbyEmailAndPassword(String email,String password){
        Account accUser = null;
        try{
            accUser = service.findByEmailAndPassword(email,password);
            user_app = service.findOneUser(accUser.getId());
        }catch(Exception e){
            user_app = null;
        }

        return user_app;
    }
    public Utilizator getUtilizatorAplicatie(){
        return user_app;
    }
}
