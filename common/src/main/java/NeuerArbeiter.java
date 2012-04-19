package akkapi.common;

import akka.actor.ActorRef;

public class NeuerArbeiter {
  public final ActorRef aktor;
  public NeuerArbeiter(ActorRef aktor) {
    this.aktor = aktor;
  }
}