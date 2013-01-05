import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "playpi"
    val appVersion      = "1.0.1"

    val appDependencies = Seq(
    	"com.typesafe.akka" % "akka-remote" % "2.0"
    )

    val common = Project(id = "common", base = file("common"))

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).dependsOn(common)
}
