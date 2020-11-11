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
import socialnetwork.repository.database.PrietenieDB;
import socialnetwork.repository.database.UtilizatorDB;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.UserServiceFullDB;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.SocialNetworkUI;
import socialnetwork.ui.SocialNetworkUIFullDB;

public class RunFromFile {
    public static void main(String[] args) {


        try {
            String fileNameUsers= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
            String fileNamePrietenie = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.prietenie");

            Repository<Long, Utilizator> userFileRepository = new UtilizatorFile(fileNameUsers
                    , new UtilizatorValidator());

            Repository<Tuple<Long,Long>, Prietenie> prietenieFileRepository = new PrietenieFile(fileNamePrietenie
                    , new PrietenieValidator(userFileRepository));

            UtilizatorService serviceUtilizator = new UtilizatorService(userFileRepository,prietenieFileRepository);

             SocialNetworkUI ui = new SocialNetworkUI();
             ui.setService(serviceUtilizator);
             ui.showUI();

        } catch (Exception e) {
            System.out.println("The App has encountered an error. Sorry." + e.getMessage());
        }


    }
}
