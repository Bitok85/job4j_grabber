package grabber.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HarbCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime localDateParse(String dateTime) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime, formatter);
        return zonedDateTime.toLocalDateTime();
    }
}
