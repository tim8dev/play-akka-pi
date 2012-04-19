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

public class Client extends UntypedActor {
  private static int genauigkeit(long n) {
    return 256;
  }

  // Brauchen wir für die Kalkulation:
  private static final BigDecimal vier = new BigDecimal(4);

  private BigDecimal kalkuliereApproximationsTeil(long von, long bis) {
    BigDecimal summe = new BigDecimal(0);
    for (long i = von; i < bis; i += 1) {
      // zaehler = (1 - (i % 2) * 2)
      BigDecimal zaehler = new BigDecimal(1 - (i % 2) * 2);
      // nenner = 2 * i + 1
      BigDecimal nenner = new BigDecimal(2 * i + 1);
      BigDecimal ergebnis = zaehler.divide(nenner, genauigkeit(i), RoundingMode.HALF_UP);
	summe = summe.add(ergebnis);
    }
    return summe.multiply(vier);
  }

  public void onReceive(Object nachricht) {
    if (nachricht instanceof Arbeit) {
      Arbeit arbeit = (Arbeit) nachricht;
      BigDecimal ergebnis = kalkuliereApproximationsTeil(arbeit.von, arbeit.bis);
      getSender().tell(new PiApproximationsTeil(arbeit.von, arbeit.bis, ergebnis), getSelf());
    } else {
      unhandled(nachricht);
    }
  }
}