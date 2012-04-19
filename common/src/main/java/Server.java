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
  private final int anzahlProPacket;
  private long anzahlNummern = 0;

  private long vergebenBis = 0;

  private BigDecimal pi = new BigDecimal(0);

  private final long beginn = System.currentTimeMillis();
  
  private final ActorRef benachrichtigen;
  private final List<ActorRef> clients = new LinkedList<ActorRef>();

  public Server(final int anzahlProPacket, ActorRef benachrichtigen) {
    this.anzahlProPacket = anzahlProPacket;
    this.benachrichtigen = benachrichtigen;
  }

  public Arbeit neueArbeit() {
    long von = vergebenBis;
    vergebenBis += anzahlProPacket;
    long bis = vergebenBis;
    return new Arbeit(von, bis);
  }

  public void neuesErgebnis() {
    System.out.println("Neue approx! [..]");
    PiApproximationsTeil piGesamt = new PiApproximationsTeil(0, anzahlNummern, pi);
    //for(ActorRef aktor: benachrichtigen) {
    benachrichtigen.tell(piGesamt);
      //}
  }

  public void onReceive(Object nachricht) {
    if (nachricht instanceof NeuerArbeiter) {
      ActorRef na = ((NeuerArbeiter) nachricht).aktor;
      System.out.println("Neuer Arbeiter: " + na);
      clients.add(na);
      // 3x damit Client immer was zu tun hat ,-)
      na.tell(neueArbeit(), getSelf());
      na.tell(neueArbeit(), getSelf());
      na.tell(neueArbeit(), getSelf());
    } else if (nachricht instanceof PiApproximationsTeil) {
      getSender().tell(neueArbeit(), getSelf());
 
      PiApproximationsTeil teil = (PiApproximationsTeil) nachricht;
      //System.out.println("Neues Teilergebnis: " + teil);
      pi = pi.add(teil.ergebnis);
      anzahlNummern += teil.laenge();

      neuesErgebnis();
    } else {
      unhandled(nachricht);
    }
  }

  public static void main(String args[]) {
    ActorSystem system = ActorSystem.create("PiSystem");
    ActorRef server = system.actorOf(new Props(Server.class), "server");
    ActorRef client1 = system.actorOf(new Props(Client.class), "client");
    server.tell(new NeuerArbeiter(client1)); 
  }
}

/**
    ActorSystem system = ActorSystem.create("PiSystem");

    // create the result listener, which will print the result and shutdown the system
    final ActorRef listener = system.actorOf(new Props(Listener.class), "listener");

    // create the master
    ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {
      public UntypedActor create() {
        return new Master(nrOfWorkers, nrOfMessages, nrOfElements, listener);
      }
    }), "master");

    // start the calculation
    master.tell(new Calculate());

  }
}
*/
