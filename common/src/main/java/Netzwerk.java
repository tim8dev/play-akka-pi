package akkapi.common;

import java.io.Console;

import com.typesafe.config.*;
import akka.actor.*;
import akka.remote.RemoteScope;

public class Netzwerk {
  public static void main(String[] args) {
    Console console = System.console();
    String eigeneIp = console.readLine("Eigene IP (siehe Desktop): ");
    String eigenerPort = console.readLine("Eigener Port (z.B. 10000): ");
    String host = console.readLine("Server-IP (10.1.12.68):");
    String port = "31337";
    String addr = "akka://akkapi@" + host + ":" + port + "/user/server";

    String configString = "akka {\n" +
       "actor {\n" +
         "provider = \"akka.remote.RemoteActorRefProvider\"\n" +
       "}\n" +
       "remote {\n" +
         "transport = \"akka.remote.netty.NettyRemoteTransport\"\n" +
         "netty {" +
           "hostname = \"" + eigeneIp + "\"\n" +
           "port = " + eigenerPort + "\n" +
         "}" +
       "}" +
      "}";

    Config config = ConfigFactory.parseString(configString);
    ActorSystem akkapi = ActorSystem.create("akkapi", config);
    System.out.println("Aktoren-System erstellt");

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

