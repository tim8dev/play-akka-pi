// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0")

resolvers += "twitter.com" at "http://maven.twttr.com/"

addSbtPlugin("com.twitter" % "sbt-package-dist" % "1.0.4")
