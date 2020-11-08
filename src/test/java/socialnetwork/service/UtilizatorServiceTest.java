package socialnetwork.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilizatorServiceTest {
    public UtilizatorService serviceUtilizator;
    @BeforeEach
    public void setUp()  {

        String fileNamePrietenie = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.prietenieTest");
        String fileNameUsers = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.usersTest");

        try {
            FileWriter fw = new FileWriter(fileNamePrietenie, false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileNamePrietenie,true))) {
            bW.write("3;4;2020-11-11 12-34");
            bW.newLine();
            bW.write("2;3;2020-11-11 12-34");
            bW.newLine();
            bW.write("1;3;2020-11-11 12-34");
            bW.newLine();
            bW.write("2;4;2020-11-11 12-34");
            bW.newLine();
            bW.write("1;4;2020-11-11 12-34");
            bW.newLine();
            bW.write("1;1045029346987152949;2020-10-27 15-13");
            bW.newLine();
            bW.write("45;8146831995662456201;2020-10-27 15-24");
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
       // rescriem usersTest


        try {
            FileWriter fw = new FileWriter(fileNameUsers, false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileNameUsers,true))) {
            bW.write("8146831995662456201;Gheorghe;awfaw");
            bW.newLine();
            bW.write("1;Aprogramatoarei;Ionut");
            bW.newLine();
            bW.write("2;Apetrei;Ileana");
            bW.newLine();
            bW.write("3;Pop;Dan");
            bW.newLine();
            bW.write("4;Zaharia;Stancunull");
            bW.newLine();
            bW.write("1045029346987152949;Gheorghe;Vasile");
            bW.newLine();
            bW.write("45;Maria;Mary");
            bW.newLine();
            bW.write("2110408562135978367;Kiki;HAlsc");
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Repository<Long, Utilizator> userFileRepository = new UtilizatorFile(fileNameUsers
                , new UtilizatorValidator());

        Repository<Tuple<Long,Long>, Prietenie> prietenieFileRepository = new PrietenieFile(fileNamePrietenie
                , new PrietenieValidator(userFileRepository));

        serviceUtilizator = new UtilizatorService(userFileRepository,prietenieFileRepository);
    }




    @Test
    @Tag("t")
    void addUtilizator() throws Exception {
        try{
            Utilizator ut  = serviceUtilizator.addUtilizator("Ana","Cameliu");
            assert  ut.getFirstName().equals("Ana");
            assert  ut.getLastName().equals("Cameliu");
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            assert ex.getMessage() == null;
        }
    }

    @Test
    @Tag("t")
    void testCreateId() {
        Long id_generat = serviceUtilizator.createId();
        assert  id_generat.toString().length() > 0;
    }

    @Test
    @Tag("t")
    void findByNumePrenume() {
        Utilizator ut = serviceUtilizator.findByNumePrenume("Zaharia","Stancunull");
        assert ut.getFirstName().equals("Zaharia");
        assert ut.getLastName().equals("Stancunull");
        assert ut.getId() == 4;
    }

    @Test
    @Tag("t")
    void getAllUsers() {
        int i=0;
        for(Utilizator s : serviceUtilizator.getAllUsers()){
            i++;
        }
        assertEquals(i,8);
    }

    @Test
    @Tag("t")
    void stergeUtilizator() {
        try {
            Utilizator ut = serviceUtilizator.stergeUtilizator("dafafgaw","gagagaw");
        }
        catch (Exception ex){
            assert ex.getMessage().length() > 0;
        }

    }

    @Test
    @Tag("t")
    void connectPrietenii() {
        serviceUtilizator.connectPrietenii();
        Utilizator xt = serviceUtilizator.findByNumePrenume("Aprogramatoarei","Ionut");
        List<Utilizator> listP =  xt.getFriends();
        assert listP.size() == 3;

        Utilizator prieten1 = serviceUtilizator.findByNumePrenume("Pop","Dan");
        Utilizator prieten2 = serviceUtilizator.findByNumePrenume("Zaharia","Stancunull");
        Utilizator prieten3 = serviceUtilizator.findByNumePrenume("Gheorghe","Vasile");


        Utilizator prietenInvalid = serviceUtilizator.findByNumePrenume("Maria","Mary");

        assert listP.contains(prieten1) == true;
        assert listP.contains(prieten2) == true;
        assert listP.contains(prieten3) == true;
        assert listP.contains(prietenInvalid) == false;
    }

    @Test
    @Tag("t")
    void findOneUser() {
        Utilizator x = serviceUtilizator.findOneUser((long)45);
        assert x.getFirstName().equals("Maria");
        assert x.getLastName().equals("Mary");

    }

    @Test
    @Tag("t")
    void getAllFriends() {
        int i=0;
        for(Prietenie s : serviceUtilizator.getAllFriends()){
            if(i==0){
                assert (long)3 == s.getId().getLeft();
                assert 4 == s.getId().getRight();
            }
            if(i==1){
                assert 2 == s.getId().getLeft();
                assert 3 == s.getId().getRight();
            }
            if(i==2){
                assert 1 == s.getId().getLeft();
                assert 3 == s.getId().getRight();
            }
            if(i==3){
                assert 2 == s.getId().getLeft();
                assert 4 == s.getId().getRight();
            }
            if(i==4){
                assert 1 == s.getId().getLeft();
                assert 4 == s.getId().getRight();
            }
            if(i==5){
                Assert.assertEquals("8146831995662456201",s.getId().getRight().toString());
            }
            if(i==6){
                Assert.assertEquals("1045029346987152949",s.getId().getRight().toString());
            }
            i++;
        }
    }

    @Test
    @Tag("t")
    void testAdaugaPrieten() throws Exception{
        int size =0;
        for(Prietenie s : serviceUtilizator.getAllFriends()) {
            size++;
        }
        assert  size == 7;


        Prietenie  ptr = serviceUtilizator.adaugaPrieten("Maria", "Mary", "Kiki", "HAlsc");

        assert ptr == null;


        int verif = -1;
        for(Prietenie s : serviceUtilizator.getAllFriends()){
            if(s.getId().getLeft().toString().equals("45") && s.getId().getRight().toString().equals("2110408562135978367")){
                verif = 1;
            }
        }
        assert verif == 1;

        size =0;
        for(Prietenie s : serviceUtilizator.getAllFriends()) {
            size++;
        }
        assert  size == 8;
    }

    @Test
    @Tag("t")
    void testStergePrietenie() throws Exception{
        this.testAdaugaPrieten();
        int size =0;
        for(Prietenie s : serviceUtilizator.getAllFriends()) {
            size++;
        }
        assert  size == 8;

        Utilizator u1 = serviceUtilizator.findByNumePrenume("Maria", "Mary");

        Utilizator u2 = serviceUtilizator.findByNumePrenume("Kiki", "HAlsc");


        Prietenie ptr = serviceUtilizator.stergePrietenie(u1.getFirstName(),u1.getLastName(),u2.getFirstName(),u2.getLastName());
        assert ptr.getId().getLeft().toString().equals("45");
        assert ptr.getId().getRight().toString().equals("2110408562135978367");
        size =0;
        for(Prietenie s : serviceUtilizator.getAllFriends()) {
            size++;
        }
        assert  size == 7;
    }

    @Test
    @Tag("t")
    void numarComunitati() {
        Integer nrC = serviceUtilizator.numarComunitati();
        assert nrC == 3;
    }

    @Test
    @Tag("t")
    void comunitateSociabila() {
        ArrayList<ArrayList<String>> comunitateSociabila = serviceUtilizator.comunitateSociabila();
        ArrayList<String> comS = serviceUtilizator.biggestArray(comunitateSociabila);
        assert comS.size() == 5;
    }

    @Test
    void biggestArray() {
    }
}