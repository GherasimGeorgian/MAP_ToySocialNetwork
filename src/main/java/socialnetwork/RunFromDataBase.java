package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryOptional;
import socialnetwork.repository.database.*;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.service.UserServiceDB;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.ui.SocialNetworkUIFullDB;

import java.util.List;

public class RunFromDataBase {
    public static void main(String[] args) {

//
//        try {

            System.out.println("Reading data from database");
            final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
            final String username= ApplicationContext.getPROPERTIES().getProperty("databse.socialnetwork.username");
            final String pasword= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.pasword");

            RepositoryOptional<Long,Utilizator> userDataBase =
                    new UtilizatorDB(url,username, pasword,  new UtilizatorValidator());


            RepositoryOptional<Tuple<Long,Long>, Prietenie> prietenieDataBase =
                    new PrietenieDB(url,username, pasword,  new PrietenieValidatorDb(userDataBase));
            RepositoryOptional<Long, Message> messageRepo =
                    new MessageDB(url,username, pasword,new MessageValidator(userDataBase));

            RepositoryOptional<Long, Invite> inviteRepo =
                    new InviteDB(url,username, pasword,new InviteValidator(userDataBase));

            RepositoryOptional<Long, Eveniment> evenimentRepo =
                    new EventDB(url,username, pasword,new EvenimentValidator());

            RepositoryOptional<Long, Account> accountRepo =
                    new AccountDB(url,username, pasword,new AccountValidator());

         //   UserServiceFullDB service3 = new UserServiceFullDB(userDataBase,prietenieDataBase,messageRepo,inviteRepo,evenimentRepo,accountRepo);

           // SocialNetworkUIFullDB ui3 = new SocialNetworkUIFullDB();
           // ui3.setService(service3);


            //ui3.showUI();



//        } catch (Exception e) {
//            System.out.println("The App has encountered an error. Sorry." + e.getMessage());
//       }


    }
}
