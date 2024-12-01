package veranstaltungen.input;

import static veranstaltungen.helper.Error.raiseBuildError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class XmlFileReader {

  private final Path inputDir;

  public XmlFileReader(String inputDirName) {
    inputDir = Path.of(inputDirName);
    if (!Files.exists(inputDir)) {
      raiseBuildError("Eingabeverzeichnis existiert nicht", inputDir);
    }
  }

  public List<String> readXmlFilesInDir() {
    try {
      List<String> input = Files.walk(inputDir)
          .filter(XmlFileReader::hasXMLExtension)
          .filter(Files::isRegularFile)
          .map(XmlFileReader::readContent)
          .toList();
      System.out.println(input.size()+" XML-Dateien gelesen ✅");
      return input;
    } catch (IOException e) {
      raiseBuildError(
          "Ladefehler beim Durchlaufen auf der Suche nach XML Dateien",
          inputDir, e);
    }
    return null;
  }


  private static String readContent(Path path) {
    try {
      String content = Files.readString(path);
      return content;
    } catch (IOException e) {
      raiseBuildError("Ladefehler beim Lesen der Datei", path);
    }
    return null;
  }

  private static boolean hasXMLExtension(Path file) {
    return file.getFileName().toString().endsWith(".xml");
  }
}
