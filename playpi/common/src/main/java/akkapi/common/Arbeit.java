package akkapi.common;

public class Arbeit implements java.io.Serializable {
  final int genauigkeit;
  final long von, bis;

  public Arbeit(long von, long bis, int genauigkeit) {
    this.von = von;
    this.bis = bis;
    this.genauigkeit = genauigkeit;
  }

  public long laenge() { return bis - von; }
}