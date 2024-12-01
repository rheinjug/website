package veranstaltungen.output;

import static veranstaltungen.helper.Error.raiseBuildError;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import veranstaltungen.Veranstaltung;

public class MarkdownFileWriter {

  private final Path outputDirectory;

  public MarkdownFileWriter(String outputDirectoryName) {
    this.outputDirectory = Path.of(outputDirectoryName);
    ensureExists(this.outputDirectory);
  }

  public void writeFiles(List<Veranstaltung> veranstaltungen) {
    for (Veranstaltung veranstaltung : veranstaltungen) {
      Path outputFile = getPathFor(veranstaltung.code());
      String content = veranstaltung.asMarkdown();
      try {
        Files.writeString(outputFile, content, StandardCharsets.UTF_8,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println(outputFile+" geschrieben ✅");
      } catch (IOException e) {
        raiseBuildError("Fehler beim Schreiben einer Markdown-Datei", outputFile, e, content);
      }
    }
  }

  private Path getPathFor(String code) {
    String filename = code + ".md";
    return outputDirectory.resolve(filename);
  }

  private void ensureExists(Path outputDirectory) {
    if (!Files.exists(outputDirectory)) {
      try {
        Files.createDirectories(outputDirectory);
      } catch (IOException e) {
        raiseBuildError(
            "Problem bei der Erzeugung des Ausgabeverzeichnisses",
            outputDirectory,
            e);
      }
    }
  }


}
