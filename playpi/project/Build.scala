import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {
    val appName         = "playpi"
    val appVersion      = "1.0.1"

    val appDependencies = Seq(
    	"com.typesafe.akka" % "akka-remote" % "2.0"
    )

    val common = Project(
      id = "common",
      base = file("common"),
      settings = com.twitter.sbt.StandardProject.newSettings
    ).settings(
      name := "AkkaPiClient",
      version := "0.1",
      mainClass := Some("akkapi.common.Netzwerk"),
      scalaVersion := "2.9.1",
      resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      libraryDependencies ++= Seq(
        "com.typesafe.akka" % "akka-actor" % "2.0",
        "com.typesafe.akka" % "akka-remote" % "2.0")
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).dependsOn(common)
}
