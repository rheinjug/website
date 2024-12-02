package veranstaltungen.helper;

import com.github.slugify.Slugify;
import java.time.format.DateTimeFormatter;
import veranstaltungen.Veranstaltung;

public class Utils {

  private static final Slugify slg = Slugify.builder().build();

  public static String slug(String s) {
    return slg.slugify(s);
  }

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static String filenameFor(Veranstaltung veranstaltung) {
    return "%s-%s".formatted(
        veranstaltung.datum().format(FORMATTER),
        veranstaltung.titel()
    );
  }

}
