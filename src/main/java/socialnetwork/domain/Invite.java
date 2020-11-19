package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Invite extends Entity<Long>  {
    private Utilizator fromInvite;
    private Utilizator toInvite;
    private LocalDateTime dateInvite;
    private InviteStatus status;

    public Invite(Long id,Utilizator fromInvite,Utilizator toInvite,LocalDateTime dateInvite,InviteStatus status){
        this.setId(id);
        this.fromInvite = fromInvite;
        this.toInvite = toInvite;
        this.dateInvite = dateInvite;
        this.status = status;
    }

    public Utilizator getFromInvite() {
        return fromInvite;
    }

    public void setFromInvite(Utilizator fromInvite) {
        this.fromInvite = fromInvite;
    }

    public Utilizator getToInvite() {
        return toInvite;
    }

    public void setToInvite(Utilizator toInvite) {
        this.toInvite = toInvite;
    }

    public LocalDateTime getDateInvite() {
        return dateInvite;
    }

    public void setDateInvite(LocalDateTime dateInvite) {
        this.dateInvite = dateInvite;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public void setStatus(InviteStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String data_mesaj = dateInvite.format(formatter);
        return new String(getId().toString() + " ->invitatie de la " + fromInvite.getFirstName() + " " + fromInvite.getLastName() + " "  + data_mesaj + " " + status.toString());
    }
}
