package akkapi.common;

import java.io.Console;

import com.typesafe.config.*;
import akka.actor.*;
import akka.remote.RemoteScope;

/**
 * The main class (entry point for the clients).
 * You can connect many clients listening on different ports (use as many clients as cores you have).
 * Try connecting from different computers!
 */
public class Netzwerk {
  public static void main(String[] args) {
    Console console = System.console();
    String name = console.readLine("Name [azAZ09]: ");
    String eigeneIp = console.readLine("your IP: ");
    //String eigeneIp = "127.0.0.1";
    String eigenerPort = console.readLine("your port: ");
    if(eigenerPort.trim() == "") {
        eigenerPort = "31331";
    }
    String host = console.readLine("server IP:");
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
    
    ActorRef client = akkapi.actorOf(new Props(Client.class), name);

    System.out.println("Client erstellt");

    System.out.println("Joining...");
    server.tell(new NeuerArbeiter(client));
  }
}

