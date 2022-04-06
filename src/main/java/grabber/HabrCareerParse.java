package grabber;

import grabber.utils.HarbCareerDateTimeParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class HabrCareerParse {

    private  static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK =
            String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        HarbCareerDateTimeParser dtParser = new HarbCareerDateTimeParser();
        for (int i = 1; i <= 5; i++) {
            Connection connection =
                    Jsoup.connect(String.format("%s%s", PAGE_LINK, "?page=" + i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element dateElement = row.select(".vacancy-card__date").first().child(0);
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf(
                        "%s %s%n %s%n",
                        vacancyName,
                        link,
                        dtParser.localDateParse(dateElement.attr("datetime")));
            });
        }
    }
}

