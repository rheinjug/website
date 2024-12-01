package veranstaltungen.input;

import static veranstaltungen.helper.Error.raiseBuildError;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import veranstaltungen.Reihe;
import veranstaltungen.Veranstaltung;

public class XmlParser {

  private static final DocumentBuilder BUILDER = initBuilder();
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");


  private static DocumentBuilder initBuilder() {
    try {
      return DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      raiseBuildError("SAX-Initialisiserung fehlgeschlagen", e);
    }
    return null;
  }

  public Veranstaltung parse(String content) {
    try {
      Document parse = BUILDER.parse(new InputSource(new StringReader(content)));
      Veranstaltung veranstaltung = readVeranstaltung(parse.getDocumentElement());
      return veranstaltung;
    } catch (SAXException e) {
      raiseBuildError("Parse Fehler in Eingabe", content, e);
    } catch (IOException e) {
      // cannot happen, input is a string
    }
    return null;
  }

  private static Veranstaltung readVeranstaltung(Element root) {
    String titel = text(root.getElementsByTagName("titel"));
    String sprecher = text(root.getElementsByTagName("sprecher"));
    String inhalt = text(root.getElementsByTagName("abstract"));
    String bio = text(root.getElementsByTagName("bio"));
    String ort = text(root.getElementsByTagName("ort"));
    LocalDate datum = LocalDate.parse(text(root.getElementsByTagName("datum")), DATE_FORMATTER);
    LocalTime uhrzeit = LocalTime.parse(text(root.getElementsByTagName("uhrzeit")), TIME_FORMATTER);
    Reihe reihe = Reihe.valueOf(text(root.getElementsByTagName("reihe")).toUpperCase());

    return new Veranstaltung(datum, uhrzeit, titel, sprecher, bio, inhalt, ort, reihe);
  }

  private static String text(NodeList element) {
    return element.item(0).getTextContent();
  }


  public List<Veranstaltung> parse(List<String> xmlContent) {
    List<Veranstaltung> parsed = xmlContent.stream().map(this::parse).toList();
    System.out.println(parsed.size()+" XML-Dateien geparsed ✅");
    return parsed;
  }
}
