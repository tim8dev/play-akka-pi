package akkapi.common;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

// Wichtige Klassen importieren
import akka.actor.*;
import akka.util.Duration;

public class Server extends UntypedActor {
    private final int genauigkeit;
    private final int anzahlProPacket;
    private long anzahlNummern = 0;

    private boolean gestartet = false;
    private long vergebenBis = 0;

    private final Map<Long, ActorRef> vergabe = new HashMap<Long, ActorRef>();

    private BigDecimal pi = new BigDecimal(0);

    private final ActorRef benachrichtigen;
    private final List<ActorRef> clients = new LinkedList<ActorRef>();
    private int freierClient = 0;

    private final Set<ActorRef> offline = new HashSet<ActorRef>();

    public Server(int anzahlProPacket, int genauigkeit, ActorRef benachrichtigen) {
        this.anzahlProPacket = anzahlProPacket;
        this.genauigkeit = genauigkeit;
        this.benachrichtigen = benachrichtigen;

        System.out.println("Server ist bereit :)");
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
            benachrichtigen.tell("online", na);
            online(na);
        } else if (nachricht instanceof Statistik) {
            benachrichtigen.tell(nachricht, getSender());
        } else if (nachricht instanceof Summand) {
            neueArbeit(getSender());

            Summand teil = (Summand) nachricht;

            ActorRef zugeteiltAn = vergabe.remove(teil.von);
            if(zugeteiltAn != null) {
                pi = pi.add(teil.ergebnis);
                anzahlNummern += teil.laenge;
            } else {
                System.out.println("Bereits erledigt!");
                online(getSender());
            }

            neuesErgebnis();
        } else if (nachricht instanceof Long) {
            // Nicht rechtzeitig angekommen!
            Long l = (Long) nachricht;
            if(vergabe.containsKey(l)) {
                ActorRef ersatz = freierClient();
                verteileArbeitAn(arbeitVon(l, ersatz), ersatz);
            } else {
                // Alles im grünen Bereich :-)
            }
            offline(getSender());
        } else {
            unhandled(nachricht);
        }
    }

    public ActorRef freierClient() {
        if(clients.isEmpty()) {
            return context().system().deadLetters();
        } else {
            freierClient += 1;
            return clients.get(freierClient % clients.size());
        }
    }

    public void neueArbeit(ActorRef client) {
        Arbeit arbeit = arbeitVon(vergebenBis,  client);
        vergebenBis += arbeit.laenge;
        verteileArbeitAn(arbeit, client);
    }

    public Arbeit arbeitVon(Long von, ActorRef client) {
        // Die ersten x Pakete mit Paketgröße < 4, damit wir schnell eine gute Approximation kriegen.
        long laenge = von < anzahlProPacket ? 1 : anzahlProPacket;
        vergabe.put(von, client);
        return new Arbeit(von, laenge, genauigkeit);
    }

    public void verteileArbeitAn(final Arbeit a, final ActorRef client) {
        final ActorRef self = getSelf();
        context().system().scheduler().scheduleOnce(Duration.create(32000, TimeUnit.MILLISECONDS), new Runnable() {
            @Override
            public void run() {
                self.tell(a.von, client);
            }
        });
        client.tell(a, self);
    }

    public void neuesErgebnis() {
        Summand piGesamt = new Summand(0, anzahlNummern, pi);
        benachrichtigen.tell(piGesamt);
    }

    public void online(ActorRef vermisst) {
        //benachrichtigen.tell("online", vermisst);
        clients.add(vermisst);
        offline.remove(vermisst);
    }

    public void offline(ActorRef vermisst) {
        //benachrichtigen.tell("offline", vermisst);
        clients.remove(vermisst);
        offline.add(vermisst);
    }
}

