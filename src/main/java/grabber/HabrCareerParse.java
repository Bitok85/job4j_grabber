package grabber;

import grabber.utils.DateTimeParser;
import grabber.utils.HarbCareerDateTimeParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class HabrCareerParse implements Parse {

    private static final Logger LOG = LoggerFactory.getLogger(HabrCareerParse.class.getName());
    private  static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK =
            String.format("%s/vacancies/java_developer", SOURCE_LINK);
    private static final int PAGES_AMOUNT = 1;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse parse = new HabrCareerParse(new HarbCareerDateTimeParser());
        System.out.println(parse.list(PAGE_LINK).get(0).getCreated());
    }

    @Override
    public List<Post> list(String link) {
        List<Post> resultList = new ArrayList<>();
        try {
            for (int i = 1; i <= PAGES_AMOUNT; i++) {
                Connection connection =
                        Jsoup.connect(String.format("%s%s", PAGE_LINK, "?page=" + i));
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> {
                    try {
                        resultList.add(postParse(row));
                    } catch (IOException e) {
                        LOG.error("I0Exception", e);
                    }
                });
            }
        } catch (IOException e) {
            LOG.error("I0Exception", e);
        }
        return resultList;
    }

    public Post postParse(Element element) throws IOException {
        return new Post(
                retrieveTitle(element),
                retrieveLink(element),
                retrieveDescription(element),
                retrieveDateTime(element)
        );
    }

    private String retrieveTitle(Element element) {
        return element.select(".vacancy-card__title").first().text();
    }

    private String retrieveLink(Element element) {
        return element.select(".vacancy-card__title").first().child(0).attr("href");
    }

    private String retrieveDescription(Element element) throws IOException {
        Element descriptionElement =
                element.select(".vacancy-card__icon-link").first().child(0);
        Element linkElement = element.select(".vacancy-card__title").first().child(0);
        String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String descriptionLink =
                String.format("%s%s", link, descriptionElement.attr("href"));
        Connection connection = Jsoup.connect(descriptionLink);
        Document document = connection.get();
        return document.select(".job_show_description__body").first().text();
    }

    private LocalDateTime retrieveDateTime(Element element) {
        Element dateElement =
                element.select(".vacancy-card__date").first().child(0);
        return dateTimeParser.localDateParse((dateElement.attr("datetime")));
    }
}

