package socialnetwork.domain;

import java.time.LocalDateTime;

public class PrietenieDTO {

    private String firstName;
    private String lastName;
    private LocalDateTime date;

    public PrietenieDTO(String firstName, String lastName, LocalDateTime date){
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDateTime getDate() {
        return date;
    }
    @Override
    public String toString() {
        return new String(this.firstName + " | " +  this.lastName + " | " + this.date);
    }
}
