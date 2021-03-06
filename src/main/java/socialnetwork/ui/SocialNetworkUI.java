package socialnetwork.ui;

import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.repo_validators.RepositoryException;
import socialnetwork.service.UtilizatorService;

import java.util.ArrayList;
import java.util.Scanner;

public class SocialNetworkUI {
    private UtilizatorService userService;


    public void setService(UtilizatorService service) {
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
            System.out.println("Eroare la introducerea datelor!");
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



//            for(ArrayList<String> listofList : result){
//                System.out.println("Comunitate next:");
//                for(String str : listofList){
//                    System.out.println(str);
//                }
//                System.out.printf("%n");
//            }
            userService.biggestArray(result).forEach(System.out::println);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
