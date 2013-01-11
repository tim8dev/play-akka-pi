package akkapi.common;

import java.math.BigDecimal;

public class Summand implements java.io.Serializable {
  public final long von, laenge;
  public final BigDecimal ergebnis;

  public Summand(long von, long laenge, BigDecimal ergebnis) {
    this.von = von;
    this.laenge = laenge;
    this.ergebnis = ergebnis;
  }

  public long bis() { return von + laenge; }
}