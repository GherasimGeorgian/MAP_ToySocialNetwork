package socialnetwork.repository.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.service.UtilizatorService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UtilizatorFileTest {

    Repository<Long, Utilizator> userFileRepository;
    @BeforeEach
    public void setUp()  {
        String fileNameUsers= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
         userFileRepository = new UtilizatorFile(fileNameUsers, new UtilizatorValidator());
    }


    @Test
    @Tag("s")
    void extractEntity() {
        ArrayList<String> arrList = new ArrayList<>(){
            {
                add("1");
                add("Hana");
                add("Montana");
            }
        };
        //Utilizator user1 = userFileRepository.extractEntity(arrList);
      //  assert user1.getFirstName().equals("Hana");
       // assert user1.getLastName().equals("Montana");
       // assert user1.getId().toString().equals("1");

    }

    @Test
    void createEntityAsString() {
    }
}