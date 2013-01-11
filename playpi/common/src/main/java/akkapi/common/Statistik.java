package akkapi.common;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * On 11.01.13 at 12:41, tim created this.
 */
public class Statistik implements Serializable {
  public final long geschwindigkeit, anzahl;
  public final BigDecimal summand;

  public Statistik(long v, long n, BigDecimal s) {
      geschwindigkeit = v;
      anzahl = n;
      summand = s;
  }
}
