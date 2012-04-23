package akkapi.common;

import java.math.BigDecimal;

public class PiApproximationsTeil implements java.io.Serializable {
  public final long von, bis;
  public final BigDecimal ergebnis;

  public PiApproximationsTeil(long von, long bis, BigDecimal ergebnis) {
    this.von = von;
    this.bis = bis;
    this.ergebnis = ergebnis;
  }

  public long laenge() { return bis - von; }
}