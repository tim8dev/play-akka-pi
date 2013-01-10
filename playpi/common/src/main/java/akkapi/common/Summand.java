package akkapi.common;

import java.math.BigDecimal;

public class Summand implements java.io.Serializable {
  public final long von, bis;
  public final BigDecimal ergebnis;

  public Summand(long von, long bis, BigDecimal ergebnis) {
    this.von = von;
    this.bis = bis;
    this.ergebnis = ergebnis;
  }

  public long laenge() { return bis - von; }
}