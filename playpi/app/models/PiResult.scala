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

case class PiApprox(approx: BigDecimal, n: Long)

class PiResultListener extends Actor {
  val i = 8192 
  val genauigkeit = 200

  var pushTo: List[PushEnumerator[JsValue]] = Nil
  def newEnum() = Enumerator.imperative[JsValue]()
  val server: ActorRef = context.system.actorOf(Props(new Server(i, genauigkeit, self)), name = "server")
  //val fastClient: ActorRef = context.actorOf(Props(new FastClient(genauigkeit)), name = "fast")

  var curResult: PiApprox = PiApprox(22.0/7, 0)

  def receive = {
    case 'start =>
      server ! "start"
    case 'join =>
      val enum = newEnum()
      pushTo = enum :: pushTo
      sender ! enum
      self ! curResult
    case approx @ PiApprox(pi, n) =>
      curResult = approx
      val msg = JsObject(
	Seq("pi" -> JsArray(prettifyPi(pi)), "n" -> JsString(prettifyInt(n)))
      )
      pushTo foreach { _.push(msg) }
    case teil : PiApproximationsTeil =>
      //self ! PiApprox(BigDecimal(1).underlying.divide(teil.ergebnis, genauigkeit, RoundingMode.HALF_UP), teil.bis)
      self ! PiApprox(teil.ergebnis, teil.bis)
    case 'stop =>
      ()
  }

  def prettifyInt(n: Long) =
    n.toString.reverse.grouped(3).mkString(".").reverse

  def prettifyPi(pi: BigDecimal) = {
    val str = pi.toString
    val first = str.take(102)
    val lines = first :: str.drop(102).grouped(100).toList
    val indexedLines = lines.zipWithIndex map { case (str, i) =>
      val idx = if(i < 9) "0" + (i + 1) else "" + (i + 1) 
      "(" + idx + ".) " + (if(i == 0) "" else "__") + str
    }
    indexedLines.map(JsString(_))
  }
}
