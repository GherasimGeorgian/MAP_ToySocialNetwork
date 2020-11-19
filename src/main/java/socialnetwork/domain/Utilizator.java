package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long> {

    private String firstName;
    private String lastName;
    private List<Utilizator> friends;

    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.friends = new ArrayList<Utilizator>();
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

    public List<Utilizator> getFriends() {
        return friends;
    }

    public void addFriend(Utilizator utilizator){
        friends.add(utilizator);
    }
    public void deleteAllFriends(){
        friends.clear();
    }

    public void setFriends(List<Utilizator> friends) {
        this.friends = friends;
    }
    @Override
    public String toString() {
        return "Utilizator{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", friends=" + getFriendsString() +
                '}';
    }
    public String getFriendsString(){
        String s = new String();
        for(Utilizator x : friends){
            s+= "["+x.getFirstName() +" " + x.getLastName() + "]";
        }
        return s;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),getFirstName(), getLastName());
    }
}