package akkapi.common;

import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

// Wichtige Klassen importieren
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;
import java.util.concurrent.TimeUnit;

/**
   Implementiert:
   http://en.wikipedia.org/wiki/Borwein%27s_algorithm
   "Another formula (1989)
 */
public class FastClient extends Client {
  public FastClient(int genauigkeit) {
    super(genauigkeit); 
  }

  private static BigDecimal eins = new BigDecimal(1);
  private static BigDecimal zwoelf = new BigDecimal(12);

  private static BigDecimal wurzel61 = new BigDecimal(Math.sqrt(61.0));

  private static BigDecimal A =
    new BigDecimal("212175710912").multiply(wurzel61).add(new BigDecimal("1657145277365"));
  private static BigDecimal B =
    new BigDecimal("13773980892672").multiply(wurzel61).add(new BigDecimal("107578229802750"));
  private static final BigDecimal C1 = new BigDecimal(5280);
  private static final BigDecimal C2 =
    new BigDecimal(236674).add(new BigDecimal(30303).multiply(wurzel61));
  private static final BigDecimal C = C1.multiply(C2).pow(3);

  private static BigDecimal fakultaet(BigDecimal n) {
    if(n.signum() == 0) {
      return eins;
    }
    BigDecimal produkt = n;
    while(n.signum() > 0) {
      produkt = produkt.multiply(n);
      n = n.subtract(eins);
    }
    return produkt;
  }

  @Override
  protected BigDecimal kalkuliereApproximationsTeil(long von, long bis) {
    /*  return calc(von, bis);
  }
    
  public static BigDecimal calc(long von, long bis) {*/
    BigDecimal summe = new BigDecimal(0);
    for (long i = von; i < bis; i += 1) {
      BigDecimal I = new BigDecimal(i);
      BigDecimal z1 = fakultaet(new BigDecimal(6*i)); // (6n)!
      BigDecimal z2 = A.add(I.multiply(B)); // A + nB
      
      BigDecimal n1 = fakultaet(I).pow(3); // (n!)^3
      BigDecimal n2 = fakultaet(I.multiply(new BigDecimal(3))); // (3n)!
      BigDecimal n3 = C.pow((int) i); // C^n * C^(1/2)
      BigDecimal n4 = new BigDecimal(Math.pow(C.doubleValue(), 0.5));

      BigDecimal z = z1.multiply(z2);
      BigDecimal n = n1.multiply(n2).multiply(n3).multiply(n4);
      BigDecimal e = z.divide(n, genauigkeit, RoundingMode.HALF_UP);
      if(i % 2 == 0) {
	summe = summe.add(e);
      } else {
	summe = summe.subtract(e);
      }
    }
    return summe.multiply(zwoelf);//, genauigkeit, RoundingMode.HALF_UP);
  }
}