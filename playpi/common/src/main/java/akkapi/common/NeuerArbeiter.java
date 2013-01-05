package akkapi.common;

import java.io.Serializable;

import akka.actor.ActorRef;

public class NeuerArbeiter implements Serializable {
  public final String name;
  public final ActorRef aktor;

  public NeuerArbeiter(String name, ActorRef aktor) {
    this.name = name;
    this.aktor = aktor;
  }

  public NeuerArbeiter(ActorRef aktor) {
    this("anonym", aktor);
  }
}