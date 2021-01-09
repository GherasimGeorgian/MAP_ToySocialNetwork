package socialnetwork.utils.threads;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.controller.UserPageController;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UserServiceFullDB;

public class NewThreadWindow {
    private UserServiceFullDB service;
    private Utilizator user_app=null;
    public NewThreadWindow(UserServiceFullDB service,Utilizator user_app){
        this.service = service;
        this.user_app = user_app;
    }
    public void execute(){

        Task<Boolean> loginTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return true;
            }
        };

        loginTask.setOnSucceeded(e -> {

            if (loginTask.getValue()) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/views/userpage.fxml"));
                    AnchorPane root = fxmlLoader.load();

                    UserPageController ctrl = fxmlLoader.getController();
                    ctrl.setService(service, user_app);

                    Stage stagePageUser = new Stage();
                    Scene scene = new Scene(root, 1271, 562);

                    stagePageUser.setTitle("UserPage");
                    stagePageUser.setScene(scene);
                    stagePageUser.show();
                }catch(Exception ex){
                    System.out.println(ex);
                }
            } else {
                //TODO
            }

        });

        loginTask.setOnFailed(e -> {

        });

        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();

    }
}

