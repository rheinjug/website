package veranstaltungen.output;

import static veranstaltungen.helper.Error.raiseBuildError;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import veranstaltungen.Veranstaltung;

public class MarkdownZipFileWriter {

  private final Path outputFile;

  public MarkdownZipFileWriter(String fileName) {
    this.outputFile = Path.of(fileName);
  }

  public void writeFiles(List<Veranstaltung> veranstaltungen) {
    try (FileOutputStream fos = new FileOutputStream(outputFile.toFile());
         ZipOutputStream zos = new ZipOutputStream(fos)) {

      for (Veranstaltung v : veranstaltungen) {
        ZipEntry zipEntry = new ZipEntry("veranstaltungen/%s.md".formatted(v.code()));
        zos.putNextEntry(zipEntry);
        zos.write(v.asMarkdown().getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
      }
    } catch (IOException e) {
      raiseBuildError("Es gab ein Problem bei der Erzeugung der" +
          " ZIP Datei mit den Veranstaltungen", e);
    }
    System.out.println(outputFile+" geschrieben ✅");
  }
}
