package models;

import java.math.RoundingMode
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
import akka.remote.RemoteActorRefProvider

case class PiApprox(approx: BigDecimal, n: Long)

class PiResultListener extends Actor {
  val i = 512
  val genauigkeit = 150

  var pushTo: List[PushEnumerator[JsValue]] = Nil

  def newEnum() = Enumerator.imperative[JsValue]()
  val server: ActorRef = context.system.actorOf(Props(new Server(i, genauigkeit, self)), name = "server")

  var curResult: PiApprox = PiApprox(22.0/7, 0)

  def receive = {
    case 'start =>
      server ! "start"
    case 'join =>
      val enum = newEnum()
      pushTo = enum :: pushTo
      Logger.info("Enumerators: " + pushTo)
      sender ! enum
      self ! curResult
    case evt: String =>
      self forward new Statistik(0, 0, BigDecimal(0).underlying())
    case stat: Statistik if sender != context.system.deadLetters =>
      val who = sender
      val msg = JsObject(
        Seq("who" -> JsString(who.path.name),
            "pi" -> JsString(stat.summand.toString.take(16)),
            "n" -> JsString(prettifyInt(stat.anzahl)),
            "v" -> JsString(prettifyInt(stat.geschwindigkeit))) ++
          who.path.address.host.map(host => "host" -> JsString(host)) ++
          who.path.address.port.map(port => "port" -> JsNumber(port))
      )
      pushTo foreach { _.push(msg) }
    case approx @ PiApprox(pi, n) =>
      curResult = approx
      val msg = JsObject(
        Seq("pi" -> JsArray(prettifyPi(pi)), "n" -> JsString(prettifyInt(n)))
      )
      pushTo foreach { _.push(msg) }
    case teil : Summand =>
      //self ! PiApprox(BigDecimal(1).underlying.divide(teil.ergebnis, genauigkeit, RoundingMode.HALF_UP), teil.bis)
      self ! PiApprox(teil.ergebnis, teil.bis)
    case 'stop =>
      ()
  }

  def prettifyInt(n: Long): String =
    n.toString.reverse.grouped(3).mkString(".").reverse

  def prettifyPi(pi: BigDecimal) = {
    val str = pi.toString
    val first = str.take(52)
    val lines = first :: str.drop(52).grouped(50).toList
    val indexedLines = lines.zipWithIndex map { case (str, i) =>
      val idx = if(i < 9) "0" + (i + 1) else "" + (i + 1) 
      "(" + idx + ".) " + (if(i == 0) "" else "__") + str
    }
    indexedLines.map(JsString(_))
  }
}
