package socialnetwork.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Prietenie extends Entity<Tuple<Long,Long>> {
    LocalDateTime date;

    public Prietenie(long u1, long u2) {
        setId(new Tuple<Long,Long>(u1,u2));
    }
    public Prietenie(long u1, long u2,LocalDateTime ldt) {
        setId(new Tuple<Long,Long>(u1,u2));
        this.date = ldt;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Prietenie{" +
                "user1='" + this.getId().getLeft() + '\'' +
                ", user2='" + this.getId().getRight() + '\'' +
                ",date = " + this.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                '}';
    }
}
