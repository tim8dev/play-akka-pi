package akkapi.common;

public class Bruch {
  private final int z, n;

  public static Bruch bruch(int z, int n) {
    Bruch(z, n).gekuerzt();
  }

  private Bruch(int z, int n) {
    this.z = z;
    this.n = n;
  }

  public Bruch add(Bruch b) {
    return Bruch(z * b.n + b.z * n, n * b.n).gekuerzt();
  }

  public Bruch mult(Bruch b) {
    return Bruch(z * b.z, n * b.n).gekuerzt();
  }

  public BigDecimal dezimal(int n) {
    BigDecimal zaehler = new BigDecimal(z);
    BigDecimal nenner = new BigDecimal(n);
    return zahler.divide(nenner, n, RoundingMode.HALF_UP); 
  }

  private Bruch gekuerzt() {
    int ggt = ggt(z, n);
    return Bruch(z / ggt, n / ggt);
  }

  private static int ggt(int a, int b) {
    if(b == 0) { return a; }
    else {
      return ggt(b, a - b);
    }
  }

}