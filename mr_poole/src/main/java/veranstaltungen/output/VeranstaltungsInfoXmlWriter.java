package veranstaltungen.output;

import static veranstaltungen.helper.Error.raiseBuildError;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import veranstaltungen.Veranstaltung;

public class VeranstaltungsInfoXmlWriter {

  public static final String XML_ROOT_ELEMENT = "veranstaltungen";
  private final Path outputFile;
  private static final DocumentBuilder BUILDER = initBuilder();


  private static DocumentBuilder initBuilder() {
    try {
      return DocumentBuilderFactory.newInstance()
          .newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      raiseBuildError("SAX-Initialisiserung fehlgeschlagen", e);
    }
    return null;
  }

  public VeranstaltungsInfoXmlWriter(String outputFileName) {
    this.outputFile = Path.of(outputFileName);
  }

  public void writeXml(List<Veranstaltung> veranstaltungen) {
    Document document = BUILDER.newDocument();
    Element root = document.createElement(XML_ROOT_ELEMENT);
    document.appendChild(root);
    veranstaltungen.stream()
        .map(v -> createElement(document, v))
        .forEach(root::appendChild);
    String xmlString = createXmlString(document);
    writeToFile(xmlString);

  }

  private void writeToFile(String xmlString) {
    try {
      Files.writeString(outputFile, xmlString, StandardCharsets.UTF_8,
          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      System.out.println(outputFile+" geschrieben ✅");
    } catch (IOException e) {
      raiseBuildError("Fehler beim Schreiben der XML Übersichts-Datei",
          outputFile.toFile(), e, xmlString);
    }
  }

  private static String createXmlString(Document document) {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();

    StringWriter writer = new StringWriter();
    StreamResult streamResult = new StreamResult(writer);

    Transformer transformer = null;
    try {
      transformer = transformerFactory.newTransformer();
    } catch (TransformerConfigurationException e) {
      raiseBuildError("XML Transformer konnte nicht erzeugt werden", e);
    }
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    try {
      transformer.transform(new DOMSource(document), streamResult);
    } catch (TransformerException e) {
      raiseBuildError("Fehler beim Erzeugen der Veranstaltungsliste", e);
    }
    return writer.getBuffer().toString();
  }

  private Element createElement(Document document, Veranstaltung veranstaltung) {
    Element vnode = document.createElement("veranstaltung");
    vnode.setAttribute("reihe", veranstaltung.reihe().toString());
    vnode.setAttribute("code", veranstaltung.code());
    vnode.setAttribute("termin",
        LocalDateTime.of(veranstaltung.datum(), veranstaltung.uhrzeit()).toString());
    vnode.setAttribute("ort", veranstaltung.ort());
    vnode.setAttribute("sprecher", veranstaltung.sprecher());
    vnode.setAttribute("titel", veranstaltung.titel());
    return vnode;
  }


}
