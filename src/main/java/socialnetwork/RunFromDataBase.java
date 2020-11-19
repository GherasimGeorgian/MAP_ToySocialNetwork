package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.InviteValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.PrietenieValidatorDb;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryOptional;
import socialnetwork.repository.database.InviteDB;
import socialnetwork.repository.database.MessageDB;
import socialnetwork.repository.database.PrietenieDB;
import socialnetwork.repository.database.UtilizatorDB;
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



            UserServiceFullDB service3 = new UserServiceFullDB(userDataBase,prietenieDataBase,messageRepo,inviteRepo);

            SocialNetworkUIFullDB ui3 = new SocialNetworkUIFullDB();
            ui3.setService(service3);


            ui3.showUI();



//        } catch (Exception e) {
//            System.out.println("The App has encountered an error. Sorry." + e.getMessage());
//       }


    }
}
