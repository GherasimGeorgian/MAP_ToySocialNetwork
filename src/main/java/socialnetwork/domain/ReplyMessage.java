package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReplyMessage extends Message{
    //trebuie sa am tabel separat in baza de date pt replymessage
    // sau ii stochez in acelasi tabel cu messages si mai adaug un camp
    // reply cu id la mesajul la care se da reply
    private Message replyMessage;

    public ReplyMessage(Message msg,Long id,Utilizator from, List<Utilizator> to, String message, LocalDateTime date){
        super(msg.getId(), msg.getFrom(), msg.getTo(), msg.getMessage(), msg.getDate());
          replyMessage = new Message(id,from,to,message,date);
//        this.replyMessage.setId(id);
//        this.replyMessage.setFrom(from);
 //       this.replyMessage.setTo(to);
 //       this.replyMessage.setMessage(message);
 //       this.replyMessage.setDate(date);
    }

    public Message getReplyMessage() {
        return replyMessage;
    }

    public void setReplyMessage(Message replyMessage) {
        this.replyMessage = replyMessage;
    }

    public String toStringNames(){
        String names = new String();
        for(Utilizator user : this.replyMessage.getTo()){
            names+= user.getFirstName() + "-" + user.getLastName() + "|";
        }
        return names;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String data_mesaj = this.getDate().format(formatter);
        return new String(this.getFrom().getFirstName() + " " + this.getFrom().getLastName() +": reply la: '" + this.replyMessage.getMessage() + "' reply cu " + this.getMessage() + "->" + data_mesaj);
    }
}
