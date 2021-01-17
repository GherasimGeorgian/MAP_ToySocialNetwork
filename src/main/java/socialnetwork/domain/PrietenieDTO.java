package socialnetwork.domain;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.time.LocalDateTime;

public class PrietenieDTO {

    private String firstName;
    private String lastName;
    private LocalDateTime date;
    private String urlPhoto;
    private Image image;
    private final BooleanProperty active;

    public void setImage(Image image) {
        this.image = image;
    }

    public PrietenieDTO(String urlPh, String firstName, String lastName, LocalDateTime date){
        this.urlPhoto = urlPh;
        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;
        this.active = new SimpleBooleanProperty(false);
    }
    public Image getImage(){
        if(urlPhoto.equals("null")){
            File file1 = new File("C:/Users/ghera/IdeaProjects/MAP_ToySocialNetwork/src/main/java/socialnetwork/utils/Logare/profile.jpg");
            try {
                image = new Image(file1.toURI().toURL().toExternalForm());

            } catch (Exception ex) {

            }
            return image;
        }else{
            File file1 = new File(urlPhoto);
            try {
                image = new Image(file1.toURI().toURL().toExternalForm());

            } catch (Exception ex) {

            }
            return image;
        }


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
