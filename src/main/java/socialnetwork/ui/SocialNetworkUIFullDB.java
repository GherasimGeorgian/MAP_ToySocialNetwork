package socialnetwork.ui;


        import socialnetwork.domain.Message;
        import socialnetwork.domain.Prietenie;
        import socialnetwork.domain.PrietenieDTO;
        import socialnetwork.domain.validators.ValidationException;
        import socialnetwork.repository.repo_validators.RepositoryException;
        import socialnetwork.service.ServiceException;
        import socialnetwork.service.UserServiceDB;
        import socialnetwork.service.UserServiceFullDB;

        import java.util.*;

public class SocialNetworkUIFullDB {
    private UserServiceFullDB userService;


    public void setService(UserServiceFullDB service) {
        this.userService = service;
    }

    private String input(String prompt) {
        Scanner input = new Scanner(System.in);
        System.out.print(prompt);
        return input.nextLine();
    }

    public void printCommands() {
        System.out.println("1. Afiseaza toti utilizatori");
        System.out.println("2. Adauga un utilizator");
        System.out.println("3. Sterge un utilizator");
        System.out.println("4. Afiseaza relatiile de prietenie");
        System.out.println("5. Adauga o prietenie");
        System.out.println("6. Sterge o prietenie");
        System.out.println("7. Afisare numar de comunitati(numarul de componente conexe din graful retelei)");
        System.out.println("8. Afisare cea mai sociabila comunitate (componenta conexa din retea cu cel mai lung drum)");
        System.out.println("9. Afiseaza toate relatiile de prietenie ale unui user");
        System.out.println("10. Afiseaza toate relatiile de prietenie ale unui user intr-un interval de timp");
        System.out.println("11. Afiseaza conversatiile a doi utilizatori(cronologic)");
        System.out.println("12. Trimite un mesaj");

        System.out.println("0. Exit");
    }

    public void showUI() {
        String cmd = "";
        boolean isRunning = true;

        while (isRunning) {
            printCommands();

            cmd = input("Enter command: ");
            System.out.println();

            switch (cmd) {
                case "1":
                    afisareUtilizatori();
                    break;

                case "2":
                    adaugaUtilizator();
                    break;
                case "3":
                    stergeUtilizator();
                    break;
                case "4":
                    relatiiPrietenie();
                    break;
                case "5":
                    adaugaPrietenie();
                    break;
                case "6":
                    stergePrietenie();
                    break;
                case "7":
                    afisareNumarComunitati();
                    break;
                case "8":
                    comunitateSociabila();
                    break;
                case "9":
                    relatiiUser();
                    break;
                case "10":
                    relatiiUserFiltrare();
                    break;
                case "11":
                    afisareConversatii();
                    break;
                case "12":
                    trimiteMesaj();
                    break;
                case "0":
                    System.out.println("Bye");
                    isRunning = false;
                    break;

                default:
                    System.out.println("Invalid command!");
                    break;
            }
            System.out.println("\n--------------");

            if (cmd == "0" || isRunning == false)
                break;
        }
    }
    private void afisareUtilizatori() {
        userService.getAllUsers().forEach(System.out::println);
    }

    private void adaugaUtilizator(){
        try {
            String firstName = input("firstName = ");
            String lastName = input("lastName = ");

            userService.addUtilizator(firstName,lastName);

        }
        catch(ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (RepositoryException er){
            System.out.println("Error: " + er.getMessage());
        }
        catch(Exception e){
            System.out.println("Eroare la introducerea datelor!" + e.getMessage());
        }
    }
    private void stergeUtilizator(){
        try {


            String firstName = input("firstName = ");
            String lastName = input("lastName = ");

            userService.stergeUtilizator(firstName,lastName);

        }
        catch(ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (RepositoryException er){
            System.out.println("Error: " + er.getMessage());
        }
        catch(Exception e){
            System.out.println("Error: " +  e.getMessage());
        }
    }
    private void relatiiPrietenie(){

        userService.getAllFriends().forEach(System.out::println);
    }

    private void adaugaPrietenie() {
        try {

            String firstNameUser1 = input("firstNameUser1 = ");
            String lastNameUser1 = input("lastNameUser1 = ");

            String firstNameUser2= input("firstNameUser2 = ");
            String lastNameUser2 = input("lastNameUser2 = ");


            userService.adaugaPrieten(firstNameUser1,lastNameUser1,firstNameUser2,lastNameUser2);

        }
        catch(ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (RepositoryException er){
            System.out.println("Error: " + er.getMessage());
        }
        catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        finally {

        }

    }
    private void stergePrietenie(){
        try {
            String firstNameUser1 = input("firstNameUser1 = ");
            String lastNameUser1 = input("lastNameUser1 = ");

            String firstNameUser2= input("firstNameUser2 = ");
            String lastNameUser2 = input("lastNameUser2 = ");


            userService.stergePrietenie(firstNameUser1,lastNameUser1,firstNameUser2,lastNameUser2);
        }
        catch(ValidationException e) {
            System.out.println("Error: " + e.getMessage());
        }
        catch (RepositoryException er){
            System.out.println("Error: " + er.getMessage());
        }
        catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        finally {

        }
    }
    private void afisareNumarComunitati(){
        try{
            Integer numar_comunitati = userService.numarComunitati();
            System.out.println("Exista " + numar_comunitati  + " comunitati");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void comunitateSociabila(){
        try{
            System.out.println("Top cele mai active comunitati: ");
            ArrayList<ArrayList<String>> result = userService.comunitateSociabila();


            userService.biggestArray(result).forEach(System.out::println);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void  relatiiUser(){
        try{
            System.out.println("Da-ti numele si prenumele pentru a vedea relatiile unui user: ");
            String firstNameUser1 = input("firstNameUser1 = ");
            String lastNameUser1 = input("lastNameUser1 = ");
            System.out.println("NumePrieten|PrenumePrieten |Data de la care sunt prieteni");
            List<PrietenieDTO> ptrList=userService.relatiiUser(firstNameUser1,lastNameUser1);
            if(!(ptrList.isEmpty())) {
                for (PrietenieDTO ptr : ptrList) {
                    System.out.println(ptr);
                }
            }
            else
                System.out.println("Nu s-au gasit inregistrari");

        }catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }
    private void relatiiUserFiltrare(){
        try{
            System.out.println("Da-ti numele si prenumele pentru a vedea relatiile unui user: ");
            String firstNameUser1 = input("firstNameUser1 = ");
            String lastNameUser1 = input("lastNameUser1 = ");
            Integer luna = Integer.parseInt(input("Da-ti luna: "));

            System.out.println("NumePrieten|PrenumePrieten |Data de la care sunt prieteni");
            List<PrietenieDTO> ptrList=userService.relatiiUserFiltrate(firstNameUser1,lastNameUser1,luna);
            if(!(ptrList.isEmpty())) {
                for (PrietenieDTO ptr : ptrList) {
                    System.out.println(ptr);
                }
            }else
                System.out.println("Nu s-au gasit inregistrari");

        }catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }
    private void afisareConversatii(){
        try{
            String firstNameUser1 = input("firstNameUser1 = ");
            String lastNameUser1 = input("lastNameUser1 = ");

            Integer nr_useri = Integer.parseInt(input("Da-ti numarul de useri pentru care vreti sa trimiteti mesajul: "));


            String firstNameUser2= input("firstNameUser2 = ");
            String lastNameUser2 = input("lastNameUser2 = ");


            List<Message> mesaje = userService.afisareConversatii(firstNameUser1,lastNameUser1,firstNameUser2,lastNameUser2);

            for(Message msg: mesaje){
                System.out.println(msg);
                System.out.println('\n');
            }

        }catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }
    private void trimiteMesaj(){
        try{
            String firstNameUser1 = input("firstNameUser1 = ");
            String lastNameUser1 = input("lastNameUser1 = ");

            Integer nr_useri = Integer.parseInt(input("Da-ti numarul de useri pentru care vreti sa trimiteti mesajul: "));

            List<AbstractMap.SimpleImmutableEntry<String, String>> useri = new ArrayList<>();
            while(nr_useri>0) {
                String firstNameUser2 = input("firstNameUser2 = ");
                String lastNameUser2 = input("lastNameUser2 = ");
                useri.add(new AbstractMap.SimpleImmutableEntry<>(firstNameUser2, lastNameUser2));
                nr_useri--;
            }
            String mesaj = input("Introdu mesajul pe care doresti sa-l trimiti: ");

            String reply = input("Reply? Yes/No: ");
            Long idreply;
            if(reply.equals("yes")){
                idreply = Long.parseLong(input("Introduceti id-ul mesajului la care da-ti reply "));
            }
            else{
                idreply =(long)-1;
            }

            userService.trimiteMesaj(firstNameUser1,lastNameUser1,useri,mesaj,idreply);


        }catch (ServiceException e){
            System.out.println(e.getMessage());
        }
    }
}
