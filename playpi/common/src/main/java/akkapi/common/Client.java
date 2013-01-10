package akkapi.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Wichtige Klassen importieren
import akka.actor.UntypedActor;

public class Client extends UntypedActor {
  // Brauchen wir für die Kalkulation:
  private static final BigDecimal vier = new BigDecimal(4);
  private static final BigDecimal eins = new BigDecimal(1);
  private static final BigDecimal minusEins = eins.negate();

  public void onReceive(Object nachricht) {
    if (nachricht instanceof Arbeit) {
      if(n == 0) {
	    gestartet = System.currentTimeMillis();
      }
      Arbeit arbeit = (Arbeit) nachricht;
      BigDecimal ergebnis = kalkuliereApproximationsTeil(arbeit.von, arbeit.bis, arbeit.genauigkeit);
      getSender().tell(new Summand(arbeit.von, arbeit.bis, ergebnis), getSelf());
      neueGeschwindigkeit(arbeit.laenge());
    } else {
      unhandled(nachricht);
    }
  }

  protected BigDecimal kalkuliereApproximationsTeil(long von, long bis, int genauigkeit) {
    BigDecimal summe = new BigDecimal(0);
    for (long i = von; i < bis; i += 1) {
      // zaehler = (1 - (i % 2) * 2)
      BigDecimal zaehler = (i % 2 == 0) ? eins : minusEins;
      // nenner = 2 * i + 1
      BigDecimal nenner = new BigDecimal(2 * i + 1);
      BigDecimal ergebnis = zaehler.divide(nenner, genauigkeit, RoundingMode.HALF_UP);
      summe = summe.add(ergebnis);
    }
    return summe.multiply(vier);
  }

  // Für die Statistik:

  private long gestartet = 0;
  private long letzteAusgabe = 0;
  // Zeit in millisekunden
  private long laufzeit() {
    return System.currentTimeMillis() - gestartet;
  }  private long n = 0;

  // Geschwindigkeit in n/sec
  private long geschwindigkeit = 0;
  protected int genauigkeit = 1000;

  private void neueGeschwindigkeit(long neu) {
    n += neu;
    geschwindigkeit = (n * 1000) / laufzeit();
    if(laufzeit() - letzteAusgabe >= 1000) {
      letzteAusgabe = laufzeit();
      System.out.println("Geschwindigkeit (nur dieser client): " + geschwindigkeit + " glieder/sec");
    }
  }
}