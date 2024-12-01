package veranstaltungen;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
    return """
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
        ---
        
        ## Zusammenfassung
        
        %s
        
        ## Sprecher:in
        
        %s
        """.formatted(
        reihe,
        datum.format(MD_DATE_FORMATTER),
        titel,
        sprecher,
        code(),
        status(),
        reihe.toString().toLowerCase(),
        ort,
        uhrzeit.format(TIME_FORMATTER)
        ,inhalt
        ,sprecherBio
    );
  }

  private static final DateTimeFormatter MD_DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");


  public boolean rheinjug() {
      return reihe==Reihe.RHEINJUG;
  }
}
