import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "playpi"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
	"com.typesafe.akka" % "akka-remote" % "2.0"
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
