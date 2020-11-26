package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.PrietenieController;
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
import socialnetwork.ui.SocialNetworkUIFullDB;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/Prietenie.fxml"));
        AnchorPane root=loader.load();

        // controller set
        PrietenieController ctrl=loader.getController();
        System.out.println("Reading data from database");
        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        final String username= ApplicationContext.getPROPERTIES().getProperty("databse.socialnetwork.username");
        final String pasword= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.pasword");

        RepositoryOptional<Long, Utilizator> userDataBase =
                new UtilizatorDB(url,username, pasword,  new UtilizatorValidator());


        RepositoryOptional<Tuple<Long,Long>, Prietenie> prietenieDataBase =
                new PrietenieDB(url,username, pasword,  new PrietenieValidatorDb(userDataBase));
        RepositoryOptional<Long, Message> messageRepo =
                new MessageDB(url,username, pasword,new MessageValidator(userDataBase));

        RepositoryOptional<Long, Invite> inviteRepo =
                new InviteDB(url,username, pasword,new InviteValidator(userDataBase));



        UserServiceFullDB service3 = new UserServiceFullDB(userDataBase,prietenieDataBase,messageRepo,inviteRepo);



        ctrl.setService(service3);

        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.setTitle("Hello World");
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
