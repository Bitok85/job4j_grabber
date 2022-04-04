package grabber;

import grabber.utils.DateTimeParse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class HabrCareerParse implements DateTimeParse {

    private  static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK =
            String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private Map<String, Integer> convertMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        HabrCareerParse habrCareerParse = new HabrCareerParse();
        Connection connection = Jsoup.connect(PAGE_LINK);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element dateElement = row.select(".vacancy-card__date").first();
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = titleElement.child(0);
            String vacancyName = titleElement.text();
            LocalDate localDate = habrCareerParse.localDateParse(dateElement.text());
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            System.out.println(String.join(
                    " ",
                    vacancyName,
                    link,
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(localDate)
            ));
        });
    }

    @Override
    public LocalDate localDateParse(String parse) {
        if (convertMap.isEmpty()) {
            convertMonths();
        }
        int year = Year.now().getValue();
        String[] parseArr = parse.split(" ");
        int month = convertMap.get(parseArr[1]);
        int day = Integer.parseInt(parseArr[0]);
        return LocalDate.of(year, month, day);
    }

    private void convertMonths() {
        Integer count = 1;
        List<String> months = List.of(
                "января", "февраля",
                "марта", "апреля", "мая",
                "июня", "июля", "августа",
                "сентября", "октября", "ноября",
                "декабря"
        );
        for (String month : months) {
            convertMap.put(month, count);
            count++;
        }
    }
}
