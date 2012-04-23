package akkapi.common;

import java.io.Console;

import akka.actor.*;
import akka.remote.RemoteScope;

public class Netzwerk {
  public static void main(String[] args) {
    ActorSystem akkapi = ActorSystem.create("akkapi");
    System.out.println("Aktoren-System erstellt");

    Console console = System.console();
    System.out.println("Bitte IP-Addresse angeben:");
    String host = console.readLine();
    String port = "31337";
    String addr = "akka://akkapi@" + host + ":" + port + "/user/server";

    System.out.println("Addresse: " + addr);
    ActorRef server = akkapi.actorFor(addr);

    System.out.println("Server Aktor ist " + server);
    
    ActorRef client = akkapi.actorOf(new Props(new UntypedActorFactory() {
	public UntypedActor create() {
	  return new Client();
	}
      }), "client");

    System.out.println("Client erstellt");

    System.out.println("Joining...");
    server.tell(new NeuerArbeiter(client));
  }
}

