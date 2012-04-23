package akkapi.common;

import java.math.BigDecimal;
import java.util.*;

// Wichtige Klassen importieren
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;
import java.util.concurrent.TimeUnit;

public class Server extends UntypedActor {
  private final int genauigkeit;
  private final int anzahlProPacket;
  private long anzahlNummern = 0;

  private long vergebenBis = 0;

  private BigDecimal pi = new BigDecimal(0);

  private final long beginn = System.currentTimeMillis();
  
  private final ActorRef benachrichtigen;
  private final List<ActorRef> clients = new LinkedList<ActorRef>();

  public Server(int anzahlProPacket, int genauigkeit, ActorRef benachrichtigen) {
    this.anzahlProPacket = anzahlProPacket;
    this.genauigkeit = genauigkeit;
    this.benachrichtigen = benachrichtigen;

    System.out.println("Server ist bereit :)");
  }

  public Arbeit neueArbeit() {
    long von = vergebenBis;
    vergebenBis += anzahlProPacket;
    long bis = vergebenBis;
    return new Arbeit(von, bis, genauigkeit);
  }

  public void neuesErgebnis() {
    System.out.println("Neue approx., anzahlNummern = " + anzahlNummern);
    PiApproximationsTeil piGesamt = new PiApproximationsTeil(0, anzahlNummern, pi);
    benachrichtigen.tell(piGesamt);
  }

  public void onReceive(Object nachricht) {
    if (nachricht == "start") {
      System.out.println("Let's start!");
      for(int i = 0; i < 3; i++) {
	for(ActorRef client : clients) {
	  System.out.println("Arbeiter " + client + " kriegt mehr Arbeit");
	  client.tell(neueArbeit(), getSelf());
	}
      }
    } else if (nachricht instanceof NeuerArbeiter) {
      ActorRef na = ((NeuerArbeiter) nachricht).aktor;
      System.out.println("Neuer Arbeiter: " + na);
      clients.add(na);
    } else if (nachricht instanceof PiApproximationsTeil) {
      getSender().tell(neueArbeit(), getSelf());
 
      PiApproximationsTeil teil = (PiApproximationsTeil) nachricht;
      //System.out.println("Neues Teilergebnis: " + teil);
      pi = pi.add(teil.ergebnis);
      anzahlNummern += teil.laenge();

      neuesErgebnis();
    } else {
      System.out.println("Oh.. no: " + nachricht);
      unhandled(nachricht);
    }
  }
}

