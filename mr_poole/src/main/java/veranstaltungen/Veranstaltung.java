package veranstaltungen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record Veranstaltung(LocalDate datum,
                            LocalTime uhrzeit,
                            String titel,
                            String sprecher,
                            String sprecherBio,
                            String inhalt,
                            String ort,
                            Reihe reihe) {

  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  public String code() {
    return reihe.kuerzel() + FORMATTER.format(datum);
  }

  public String status() {
    // Erst am nächsten Tag wird eine veranstaltungen.Veranstaltung als vorbei markiert
    LocalDateTime zeitpunkt = datum.plusDays(1).atStartOfDay();
    return LocalDateTime.now().isBefore(zeitpunkt) ? "upcoming" : "past";
  }

  public String asMarkdown() {
    List<String> elements = new ArrayList<>();

    elements.add("""
        ---
        layout: post
        reihe: %s
        date: %s 14:55:05 +0100
        title: "%s"
        sprecher: "%s"
        code: "%s"
        status: "%s"
        categories: %s event
        ort: "%s"
        zeit: "%s"
        short: "%s"
        ---
        """.formatted(reihe,
        datum.format(MD_DATE_FORMATTER),
        titel,
        sprecher,
        code(),
        status(),
        reihe.toString().toLowerCase(),
        ort,
        uhrzeit.format(TIME_FORMATTER),
        shortInhalt()));
    if (inhalt != null && !inhalt.isBlank()) {
      elements.add("""
          ## Zusammenfassung
          
          %s
          """.formatted(inhalt));
    }

    if (sprecherBio != null && !sprecherBio.isBlank()) {
      elements.add("""
          
          ## Sprecher:in    
          
          %s
          """.formatted(sprecherBio));
    }
    return elements.stream().collect(Collectors.joining("\n"));
  }

  private static final DateTimeFormatter MD_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");


  public boolean rheinjug() {
    return reihe == Reihe.RHEINJUG;
  }

  public String shortInhalt() {
    if (inhalt == null || inhalt.trim().isBlank()) {
      return "";
    }
    return Arrays.stream(inhalt.split(" "))
        .limit(35)
        .collect(Collectors.joining(" "))
        .replaceAll("\n", " ")
        + " ...";
  }
}
