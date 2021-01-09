package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import socialnetwork.domain.Account;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.utils.password.PasswordHashing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreateAccountController {
    private UserServiceFullDB service;
    private Utilizator user_app;
    public void setService(UserServiceFullDB service) {
        this.service=service;
        changeImage();
    }
    public String confirmCode=null;

    @FXML
    Button btnCreate;

    @FXML
    Button btnCancel;
    @FXML
    TextField txtNume;

    @FXML
    TextField txtPrenume;

    @FXML
    TextField txtEmail;

    @FXML
    PasswordField txtParola;

    @FXML
    PasswordField txtConfirmParola;


    @FXML
    CheckBox checkBoxCondition;

    @FXML
    TextField txtConfirmCode;



    @FXML
    ImageView imageView1;
    private void changeImage(){
        try {
            List<String> listaImagini = new ArrayList<>();
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/58319.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/8otes.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/754vh.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/06453.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/20033.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/c668a.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/ck56e.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/d826d.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/d4686.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/e0ktv.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/ea861.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/egg16.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/fhc2e.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/gj0ca.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/hkl1g.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/jd3hm.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/jdgca.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/kpn8c.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/m0e04.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/m4q95.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/no9n4.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/prc0q.png");
            listaImagini.add("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/qjed8.png");
            Random rand = new Random();


            String randomElement = new String();
            int result = rand.nextInt(listaImagini.size()-1);



            randomElement = listaImagini.get(result);
            System.out.println(randomElement);

            String[] parts = randomElement.split("/");
            String imageNameAndExtension = parts[parts.length-1];
            String[] parts2 = imageNameAndExtension.split("\\.");

            String codeImage = parts2[0];
            confirmCode = codeImage;

            File file1 = new File(randomElement);


            Image a = new Image(file1.toURI().toURL().toExternalForm());
            imageView1.setImage(a);

        }catch(Exception ex){

        }
    }

    @FXML
    public void initialize() {

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) btnCancel.getScene().getWindow();
                stage.close();
            }
        });


        btnCreate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (txtConfirmCode.getText().equals(confirmCode)) {
                    if (!txtNume.getText().isEmpty() && !txtPrenume.getText().isEmpty() && !txtEmail.getText().isEmpty()
                            && !txtParola.getText().isEmpty() && !txtConfirmParola.getText().isEmpty() && checkBoxCondition.isSelected()) {
                        if (txtParola.getText().equals(txtConfirmParola.getText())) {
                            Account accRez = service.createAccount(txtNume.getText(), txtPrenume.getText(), txtEmail.getText(), PasswordHashing.doHashing(txtParola.getText()), "NORMAL");

                            if (accRez == null) {
                                MessageAlert.showErrorMessage(null, "Contul exista deja!");
                                txtNume.clear();
                                txtPrenume.clear();
                                txtParola.clear();
                                txtConfirmParola.clear();
                                txtEmail.clear();
                                checkBoxCondition.setSelected(false);
                                txtConfirmCode.clear();
                            } else {
                                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Nice", "Contul tau a fost creat cu succes!!!");
                                Stage stage = (Stage) btnCancel.getScene().getWindow();
                                stage.close();
                            }
                        } else {
                            MessageAlert.showErrorMessage(null, "Ai introdus parola gresit!");
                            Stage stage = (Stage) btnCancel.getScene().getWindow();
                            txtNume.clear();
                            txtPrenume.clear();
                            txtParola.clear();
                            txtConfirmParola.clear();
                            txtEmail.clear();
                            checkBoxCondition.setSelected(false);
                            txtConfirmCode.clear();
                        }
                    } else {
                        MessageAlert.showErrorMessage(null, "Trebuie sa completezi toate informatiile!");
                        txtConfirmCode.clear();
                        txtParola.clear();
                        txtConfirmParola.clear();
                    }

                }
                else{
                    MessageAlert.showErrorMessage(null, "Ai introdus gresit codul chapta!");
                    txtConfirmCode.clear();
                    txtParola.clear();
                    txtConfirmParola.clear();
                    changeImage();
                }
            }
        });
    }
}
