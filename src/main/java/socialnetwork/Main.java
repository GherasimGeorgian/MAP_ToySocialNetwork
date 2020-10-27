package socialnetwork;

import socialnetwork.Algorithm.Graph;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.SocialNetworkUI;

public class Main {
    public static void main(String[] args) {


        try {
            String fileNameUsers= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
            String fileNamePrietenie = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.prietenie");

            // strategy utilizator validator
            Repository<Long, Utilizator> userFileRepository = new UtilizatorFile(fileNameUsers
                    , new UtilizatorValidator());

            Repository<Tuple<Long,Long>, Prietenie> prietenieFileRepository = new PrietenieFile(fileNamePrietenie
                    , new PrietenieValidator(userFileRepository));

            UtilizatorService serviceUtilizator = new UtilizatorService(userFileRepository,prietenieFileRepository);
          //  PrietenieService servicePrietenie = new PrietenieService(prietenieFileRepository);

             SocialNetworkUI ui = new SocialNetworkUI();
             ui.setService(serviceUtilizator);
             ui.showUI();

        } catch (Exception e) {
            System.out.println("The App has encountered an error. Sorry.");
       }


    }
}
