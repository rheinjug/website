package veranstaltungen;

import java.util.List;
import veranstaltungen.input.XmlFileReader;
import veranstaltungen.input.XmlParser;
import veranstaltungen.output.MarkdownFileWriter;
import veranstaltungen.output.VeranstaltungsInfoXmlWriter;

public class Xml2Markdown {

  private final XmlFileReader source;
  private final XmlParser parser;
  private final MarkdownFileWriter markdownFileWriter;
  private final VeranstaltungsInfoXmlWriter infoXmlWriter;

  public Xml2Markdown(XmlFileReader source,
                      XmlParser parser, MarkdownFileWriter markdownFileWriter,
                      VeranstaltungsInfoXmlWriter infoXmlWriter) {
    this.source = source;
    this.parser = parser;
    this.markdownFileWriter = markdownFileWriter;
    this.infoXmlWriter = infoXmlWriter;

  }

  private void run() {
    List<String> xmlContent = source.readXmlFilesInDir();
    List<Veranstaltung> veranstaltungen = parser.parse(xmlContent);
    markdownFileWriter.writeFiles(veranstaltungen);
    infoXmlWriter.writeXml(veranstaltungen);
  }


  public static void main(String[] args) {
    new Xml2Markdown(
        new XmlFileReader("../veranstaltungen"),
        new XmlParser(),
        new MarkdownFileWriter("output/mds"),
        new VeranstaltungsInfoXmlWriter("output/index.xml"))
        .run();
  }


}
