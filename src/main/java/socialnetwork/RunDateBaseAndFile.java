package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.PrietenieValidatorDb;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryOptional;
import socialnetwork.repository.database.UtilizatorDB;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.UserServiceDB;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.SocialNetworkUI;
import socialnetwork.ui.SocialNetworkUIDb;

public class RunDateBaseAndFile {
    public static void main(String[] args) {


        try {

            final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
            final String username= ApplicationContext.getPROPERTIES().getProperty("databse.socialnetwork.username");
            final String pasword= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.pasword");
            String fileNamePrietenie = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.prietenie");

            RepositoryOptional<Long,Utilizator> userDataBase =
                    new UtilizatorDB(url,username, pasword,  new UtilizatorValidator());

            Repository<Tuple<Long,Long>, Prietenie> prietenieFileRepository2 = new PrietenieFile(fileNamePrietenie
                   , new PrietenieValidatorDb(userDataBase));
            UserServiceDB service2 = new UserServiceDB(userDataBase,prietenieFileRepository2);

            SocialNetworkUIDb ui2 = new SocialNetworkUIDb();
            ui2.setService(service2);
            ui2.showUI();

        } catch (Exception e) {
            System.out.println("The App has encountered an error. Sorry." + e.getMessage());
        }


    }
}
