package veranstaltungen.helper;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Error {


  public static void raiseBuildError(String message, Object... details) {
    System.err.println("\n\n### BUILD FAILED ###");
    System.err.println(message);
    String detailmsg = Arrays.stream(details)
        .map(Object::toString)
        .collect(
            Collectors.joining("\n-------------------------------------------------------\n"));
    System.err.println("======================================================");
    System.err.println(detailmsg);
    System.err.println("======================================================\n\n");

    System.exit(-1);
  }

}
