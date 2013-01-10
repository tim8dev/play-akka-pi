package akkapi.common;

public class Arbeit implements java.io.Serializable {
  final int genauigkeit;
  final long von, laenge;

  public Arbeit(long von, long laenge, int genauigkeit) {
    this.von = von;
    this.laenge = laenge;
    this.genauigkeit = genauigkeit;
  }

  public long bis() { return von + laenge; }
}