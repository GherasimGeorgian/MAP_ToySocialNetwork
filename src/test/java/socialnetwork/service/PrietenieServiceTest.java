package socialnetwork.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Entity;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PrietenieServiceTest {

    public PrietenieService servicePrietenie;
    @BeforeEach
    public void setUp()  {

        String fileNamePrietenie = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.prietenieTest");

        try {
            FileWriter fw = new FileWriter(fileNamePrietenie, false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileNamePrietenie,true))) {
            bW.write("3;4");
            bW.newLine();
            bW.write("2;3");
            bW.newLine();
            bW.write("1;3");
            bW.newLine();
            bW.write("2;4");
            bW.newLine();
            bW.write("1;4");
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }



        Repository<Tuple<Long,Long>, Prietenie> prietenieFileRepository = new PrietenieFile(fileNamePrietenie
                , new PrietenieValidator());


        servicePrietenie = new PrietenieService(prietenieFileRepository);


    }

    @Test
    @Tag("t")
    void getAll() {
        int i=0;
        for(Prietenie s : servicePrietenie.getAll()){
            if(i==0){
                assert 3 == s.prietenie.getLeft();
                assert 4 == s.prietenie.getRight();
            }
            if(i==1){
                assert 2 == s.prietenie.getLeft();
                assert 3 == s.prietenie.getRight();
            }
            if(i==2){
                assert 1 == s.prietenie.getLeft();
                assert 3 == s.prietenie.getRight();
            }
            if(i==3){
                assert 2 == s.prietenie.getLeft();
                assert 4 == s.prietenie.getRight();
            }
            if(i==4){
                assert 1 == s.prietenie.getLeft();
                assert 4 == s.prietenie.getRight();
            }
           i++;
        }

    }

    @Test
    @Tag("t")
    void adaugaPrieten() {
        int size =0;
        for(Prietenie s : servicePrietenie.getAll()) {
            size++;
        }
        assert  size == 5;

        Utilizator u1 = new Utilizator("Ana","Maria");
        u1.setId((long)6);

        Utilizator u2 = new Utilizator("Geo","Leo");
        u2.setId((long)7);

        Prietenie ptr = servicePrietenie.adaugaPrieten(u1,u2);

        assert ptr == null;


        int verif = -1;
        for(Prietenie s : servicePrietenie.getAll()){
            if(s.prietenie.getLeft() ==6 && s.prietenie.getRight() == 7){
                verif = 1;
            }
        }
        assert verif == 1;

         size =0;
        for(Prietenie s : servicePrietenie.getAll()) {
            size++;
        }
        assert  size == 6;
    }

    @Test
    @Tag("t")
    void stergePrietenie() {
        int size =0;
        for(Prietenie s : servicePrietenie.getAll()) {
            size++;
        }
        assert  size == 5;

        Utilizator u1 = new Utilizator("Ana","Maria");
        u1.setId((long)1);

        Utilizator u2 = new Utilizator("Geo","Leo");
        u2.setId((long)3);

        Prietenie ptr = servicePrietenie.stergePrietenie(u1,u2);
        assert ptr.prietenie.getLeft() == 1;
        assert ptr.prietenie.getRight() == 3;
        size =0;
        for(Prietenie s : servicePrietenie.getAll()) {
            size++;
        }
        assert  size == 4;
    }
}