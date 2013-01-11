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

import play.api.Play.current 

object Application extends Controller {
  implicit val timeout = Timeout(1 seconds)

  val akkapi = ActorSystem("akkapi")
  val defResult = PiApprox(BigDecimal(3.141), 0)
  val piActor: ActorRef = akkapi.actorOf(Props[PiResultListener], name = "webclient")

  def index = Action { implicit request =>
    Ok(views.html.demo(defResult))
  }

  def presentation = Action { implicit request =>
    Ok(views.html.presentation(defResult))
  }

  def start = Action { implicit request =>
    piActor ! 'start
    Ok(views.html.demo(defResult))
  }

  def result = WebSocket.async[JsValue] { request =>
    val printlnIteratee = Iteratee.foreach[JsValue](js => println(js))
    (piActor ? 'join).mapTo[Enumerator[JsValue]].asPromise map { enum => (printlnIteratee -> enum) }
  }
}
