package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.LoginStartController;
import socialnetwork.controller.PrietenieController;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.RepositoryOptional;
import socialnetwork.repository.database.*;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.ui.SocialNetworkUIFullDB;

public class MainFX extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{


        System.out.println("Reading data from database");
        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        final String username= ApplicationContext.getPROPERTIES().getProperty("databse.socialnetwork.username");
        final String pasword= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.pasword");

        PagingRepository<Long, Utilizator> userDataBase =
                new UtilizatorDB(url,username, pasword,  new UtilizatorValidator());



        PagingRepository<Tuple<Long,Long>, Prietenie> prietenieDataBase =
                new PrietenieDB(url,username, pasword,  new PrietenieValidatorDb(userDataBase));
        PagingRepository<Long, Message> messageRepo =
                new MessageDB(url,username, pasword,new MessageValidator(userDataBase));
        PagingRepository<Long, Invite> inviteRepo =
                new InviteDB(url,username, pasword,new InviteValidator(userDataBase));

        PagingRepository<Long, Eveniment> evenimentRepo =
                new EventDB(url,username, pasword,new EvenimentValidator());

        PagingRepository<Long,Account> accountRepo =
                new AccountDB(url,username,pasword, new AccountValidator());

        PagingRepository<Tuple<Long,Long>,AbonareEveniment> repoAbonareEvent =
                new AbonareEventDB(url,username,pasword, new AbonareEvenimentValidator());

        UserServiceFullDB serviceUser = new UserServiceFullDB(userDataBase,prietenieDataBase,messageRepo,inviteRepo,evenimentRepo,accountRepo,repoAbonareEvent);

       // serviceUser.abonatiEvenimente().forEach(System.out::println);
//        serviceUser.getAllUsers().forEach(System.out::println);
//        serviceUser.setPageSize(2);
//        System.out.println("Elements on page 0");
//        serviceUser.getUsersOnPage(0).stream()
//                .forEach(System.out::println);
//        System.out.println("Elements on next page");
//        serviceUser.getNextUsers().stream()
//                .forEach(System.out::println);


//        FXMLLoader loader=new FXMLLoader();
//        loader.setLocation(getClass().getResource("/views/Prietenie.fxml"));
//        AnchorPane root=loader.load();

        // controller set
//        PrietenieController ctrl=loader.getController();
//
//        ctrl.setService(serviceUser);
//
//        primaryStage.setScene(new Scene(root, 700, 500));
//        primaryStage.setTitle("Hello World");
//        primaryStage.show();

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/loginStart.fxml"));
        AnchorPane root=loader.load();


        LoginStartController ctrl=loader.getController();
        ctrl.setService(serviceUser);
        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.setTitle("LoginPage");
        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
