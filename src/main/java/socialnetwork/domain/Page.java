package socialnetwork.domain;

import socialnetwork.service.UserServiceFullDB;

import java.util.ArrayList;
import java.util.List;

public class Page {
    private String firstName;
    private String lastName;
    private List<PrietenieDTO> prieteni = new ArrayList<>();
    private List<Message> mesajePrimite = new ArrayList<>();
    private List<Invite> cereriPrietenie = new ArrayList<>();
    private UserServiceFullDB service;
    private Utilizator user;

    public Page(UserServiceFullDB serviceFullDB){
        this.service = serviceFullDB;
    }

    public void createPage(Utilizator user){
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.prieteni = service.relatiiUser(user.getFirstName(),user.getLastName());
        this.user = user;
    }
    public void actualizarePrietenii(){
        this.prieteni = service.relatiiUser(user.getFirstName(),user.getLastName());
        //this.prieteni = service.relatiiUserPageable(user,service.getCurrentPageRelatii(page));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<PrietenieDTO> getPrieteni() {
        actualizarePrietenii();
        return this.prieteni;
    }

    public void setPrieteni(List<PrietenieDTO> prieteni) {
        this.prieteni = prieteni;
    }

    public List<Message> getMesajePrimite() {
        return mesajePrimite;
    }

    public void setMesajePrimite(List<Message> mesajePrimite) {
        this.mesajePrimite = mesajePrimite;
    }

    public List<Invite> getCereriPrietenie() {
        return cereriPrietenie;
    }

    public void setCereriPrietenie(List<Invite> cereriPrietenie) {
        this.cereriPrietenie = cereriPrietenie;
    }
}
