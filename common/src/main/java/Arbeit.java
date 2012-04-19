package akkapi.common;

public class Arbeit {
  final long von, bis;

  public Arbeit(long von, long bis) {
    this.von = von;
    this.bis = bis;
  }

  public long laenge() { return bis - von; }
}