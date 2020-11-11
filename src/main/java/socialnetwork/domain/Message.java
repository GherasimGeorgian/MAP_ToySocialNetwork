package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long> {

    private Utilizator from;
    private List<Utilizator> to;
    private String message;
    private LocalDateTime date;

    public Message(Utilizator from, List<Utilizator> to,String message,LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
    }
    public Message(Long id,Utilizator from, List<Utilizator> to,String message,LocalDateTime date) {
        this.setId(id);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public String toStringNames(){
        String names = new String();
        for(Utilizator user : this.to){
            names+= user.getFirstName() + "-" + user.getLastName() + "|";
        }
        return names;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String data_mesaj = date.format(formatter);
       return new String(from.getFirstName()+" "+from.getLastName() + ": '" + message + "' -> " + data_mesaj);
    }
}
