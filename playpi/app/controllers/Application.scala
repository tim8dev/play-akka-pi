package controllers

import play.api._
import play.api.mvc._

import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import models._

import akka.actor._
import akka.util.duration._
import akka.util.Timeout
import akka.pattern.ask

import play.api.libs.concurrent.Akka
import play.libs.Akka._
import play.api.Play.current 

object Application extends Controller {
  implicit val timeout = Timeout(1 seconds)

  val defResult = PiApprox(BigDecimal(3.141), 0)

  def index = Action { implicit request =>
    Ok(views.html.index(defResult))
  }

  def result = WebSocket.async[JsValue] { request =>
    val piActor: ActorRef = Akka.system.actorOf(Props[PiResultListener])
    val printlnIteratee = Iteratee.foreach[JsValue](js => println(js))
    (piActor ? 'join).mapTo[Enumerator[JsValue]].asPromise map { enum => (printlnIteratee -> enum) }
  }
}
