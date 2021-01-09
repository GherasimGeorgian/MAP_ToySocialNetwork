package socialnetwork.domain;

import java.time.LocalDateTime;

public class Account extends Entity<Long>{

    private Long id_utilizator;
    private LocalDateTime data_creeare_cont;
    private String parola;
    private String Email;
    private String tipCont;
    private String url_photo;
    public Account(Long idUser, LocalDateTime data_cont, String email, String parola, String type,String url_photo){
        this.setId(idUser);
        this.id_utilizator= idUser;
        this.data_creeare_cont = data_cont;
        this.parola = parola;
        this.Email = email;
        this.tipCont = type;
        this.url_photo = url_photo;
    }
    public String getUrl_photo(){
        return url_photo;
    }

    public void setUrl_photo(String new_url){
        this.url_photo = new_url;
    }

    public Long getId_utilizator() {
        return id_utilizator;
    }

    public void setId_utilizator(Long id_utilizator) {
        this.id_utilizator = id_utilizator;
    }

    public LocalDateTime getData_creeare_cont() {
        return data_creeare_cont;
    }

    public void setData_creeare_cont(LocalDateTime data_creeare_cont) {
        this.data_creeare_cont = data_creeare_cont;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getTipCont() {
        return tipCont;
    }

    public void setTipCont(String tipCont) {
        this.tipCont = tipCont;
    }
}
