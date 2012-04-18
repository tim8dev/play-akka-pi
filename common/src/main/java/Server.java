/**
* Copyright (C) 2009-2012 Typesafe Inc. <http://www.typesafe.com>
*/

package akka.tutorial.first.java;

import java.math.BigDecimal

// Wichtige Klassen importieren
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;
import java.util.concurrent.TimeUnit;

public class PiApproximationsTeil {
  final long von, bis;
  final BigDecimal ergebnis;

  public PiApproximationsTeil(long von, long bis, BigDecimal ergebnis) {
    this.von = von;
    this.bis = bis;
    this.ergebnis = ergebnis;
  }

  public long laenge() { return bis - von; }
}

public class Arbeit {
  final long von, bis;

  public Arbeit(long von, long bis) {
    this.von = von;
    this.bis = bis;
  }

  public long laenge() { return bis - von; }
}

public class Server extends UntypedActor {
  private final int anzahlProPacket;
  private long anzahlNummern = 0;

  private long vergebenBis = 0;

  private BigDecimal pi = new BigDecimal(0);

  private final long beginn = System.currentTimeMillis();
  
  private final ActorRef guiAktor;
  private final List<ActorRef> clients = new LinkedList<ActorRef>();

  public Server(final int anzahlProPacket, ActorRef guiAktor) {
    this.anzahlProPacket = anzahlProPacket;
    this.guiAktor = guiAktor;
  }

  public Arbeit neueArbeit() {
    long von = vergebenBis;
    vergebenBis += anzahlProPacket;
    long bis = vergebenBis;
    return new Arbeit(von, bis);
  }

  public void neuesErgebnis() {
    System.out.println("Neue approx: " + pi);
  }

  public void onReceive(Object nachricht) {
    if (nachricht instanceof NeuerClient) {
      NeuerClient nc = (NeuerClient) nachricht;
      clients.add(nc);
      // 2x damit Client immer was zu tun hat ,-)
      nc.tell(neueArbeit());
      nc.tell(neueArbeit());
    } else if (nachricht instanceof PiApproximationsTeil) {
      getSender().tell(neueArbeit());
 
      PiApproximationsTeil teil = (PiApproximationsTeil) nachricht;
      pi = pi.plus(teil.ergebnis);
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
    server.tell(new NeuerClient(client1)); 
  }
}

public class Client extends UntypedActor {
  private static final BigDecimal vier = new BigDecimal(4);

  private BigDecimal kalkuliereApproximationsTeil(int von, int bis) {
    BigDecimal summe = new BigDecimal(0);
    for (int i = von; i < bis; i++) {
      // zaehler = (1 - (i % 2) * 2) 
      BigDecimal zaehler = BigDecimal(1).sub(BigDecimal(i % 2).mult(2));
      // nenner = 2 * i + 1
      BigDecimal nenner = BigDecimal(2).mult(i).plus(1);
      summe = summe.plus(zaehler.div(nenner));
    }
    return summe;
  }

  public void onReceive(Object nachricht) {
    if (nachricht instanceof Work) {
      Arbeit arbeit = (Arbeit) nachricht;
      BigDecimal ergebnis = kalkuliereApproximationsTeil(arbeit.von, arbeit.bis);
      getSender().tell(new PiApproximationsTeil(arbeit.von, arbeit.bis, result));
    } else {
      unhandled(message);
    }
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