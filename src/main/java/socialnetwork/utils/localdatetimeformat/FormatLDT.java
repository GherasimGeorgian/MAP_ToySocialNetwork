package socialnetwork.utils.localdatetimeformat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatLDT {

    public static String convert(LocalDateTime ldt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedString = ldt.format(formatter);
        return formattedString;
    }
}
