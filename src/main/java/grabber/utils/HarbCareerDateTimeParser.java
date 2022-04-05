package grabber.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class HarbCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime localDateParse(String dateTime) {
        return ZonedDateTime.parse(dateTime).toLocalDateTime();
    }
}
