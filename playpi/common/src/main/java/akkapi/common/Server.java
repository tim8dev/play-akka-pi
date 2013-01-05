package akkapi.common;

import java.math.BigDecimal;
import java.util.*;

// Wichtige Klassen importieren
import akka.actor.*;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;
import java.util.concurrent.TimeUnit;

public class Server extends UntypedActor {
    private final int genauigkeit;
    private final int anzahlProPacket;
    private long anzahlNummern = 0;

    private boolean gestartet = false;
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

    public void neueArbeit(ActorRef client) {
        long von = vergebenBis;
        if(vergebenBis < anzahlProPacket) {
            // Die ersten x Pakete mit Paketgröße < 4, damit wir schnell eine gute Approximation kriegen.
            vergebenBis += Math.min(anzahlProPacket, 4);
        } else {
            vergebenBis += anzahlProPacket;
        }
        long bis = vergebenBis;
        client.tell(new Arbeit(von, bis, genauigkeit), getSelf());
    }

    public void neuesErgebnis() {
        PiApproximationsTeil piGesamt = new PiApproximationsTeil(0, anzahlNummern, pi);
        benachrichtigen.tell(piGesamt);
    }

    public void onReceive(Object nachricht) {
        if (nachricht == "start") {
            System.out.println("Let's start!");
            gestartet = true;
            for(int i = 0; i < 3; i++) {
                for(ActorRef client : clients) {
                    neueArbeit(client);
                }
            }
        } else if (nachricht instanceof NeuerArbeiter) {
            ActorRef na = ((NeuerArbeiter) nachricht).aktor;
            System.out.println("Neuer Arbeiter: " + na);
            clients.add(na);
            if(gestartet) {
                neueArbeit(na);
                neueArbeit(na);
            }
        } else if (nachricht instanceof PiApproximationsTeil) {
            neueArbeit(getSender());

            PiApproximationsTeil teil = (PiApproximationsTeil) nachricht;
            pi = pi.add(teil.ergebnis);
            anzahlNummern += teil.laenge();

            neuesErgebnis();
        } else {
            unhandled(nachricht);
        }
    }
}

