package veranstaltungen;

public enum Reihe {
  RHEINJUG("RJ", "https://rheinjug.de/veranstaltung/"),
  SEP("SEP", "https://softwareentwicklung-in-der-praxis.github.io/veranstaltung/");

  private final String kuerzel;
  private final String baselink;

  Reihe(String kuerzel, String baselink) {
    this.kuerzel = kuerzel;
    this.baselink = baselink;
  }

  public String kuerzel() {
    return kuerzel;
  }

  public String baselink() {
    return baselink;
  }
}
