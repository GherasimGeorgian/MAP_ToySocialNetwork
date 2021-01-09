package socialnetwork.domain;

import socialnetwork.utils.localdatetimeformat.FormatLDT;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Eveniment extends Entity<Long> {

    String nameEvent;
    LocalDateTime dataEvent;

    public Eveniment(Long idevent,String event_name,LocalDateTime dateEvent){
        this.setId(idevent);
        this.nameEvent = event_name;
        this.dataEvent = dateEvent;

    }

    public String getNameEvent() {
        return nameEvent;
    }

    public LocalDateTime getDataEvent() {
        return dataEvent;
    }

    public String getDataString() {
        return FormatLDT.convert(dataEvent);
    }

    @Override
    public String toString() {
        return new String(getNameEvent() + " " + getDataEvent());
    }
}
