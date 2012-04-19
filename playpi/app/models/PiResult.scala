package models;

import scala.math.BigDecimal

import akka.actor._
import akka.util.duration._

import play.api._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import play.api.Play.current

import akka.util.Timeout
import akka.pattern.ask

import akkapi.common._

case class PiApprox(approx: BigDecimal, n: Long)

class PiResultListener extends Actor {
  val pushTo: PushEnumerator[JsValue] = Enumerator.imperative[JsValue]()
  val server: ActorRef =
    context.actorOf(Props(new Server(128, self)), name = "server")
  val dummyClient1: ActorRef = context.actorOf(Props[Client], name = "dummy1")
  val dummyClient2: ActorRef = context.actorOf(Props[Client], name = "dummy2")

  var curResult: PiApprox = PiApprox(3.14159, 1)

  def receive = {
    case 'join =>
      sender ! pushTo
      self ! curResult
      server ! new NeuerArbeiter(dummyClient1)
      server ! new NeuerArbeiter(dummyClient2)
    case approx @ PiApprox(pi, n) =>
      curResult = approx
      val msg = JsObject(
	Seq("pi" -> JsString(pi.toString), "n" -> JsString(n.toString))
      )
      pushTo.push(msg) 
    case teil : PiApproximationsTeil =>
      self ! PiApprox(teil.ergebnis, teil.bis)
    case 'stop =>
      ()
  }
}
