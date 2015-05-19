name := """demo-securesocial"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT",
  "org.almoehi" %% "reactive-docker" % "0.1-SNAPSHOT",
  "com.github.tototoshi" %% "play-json4s-native" % "0.3.0",
  "com.github.tototoshi" %% "play-json4s-test-native" % "0.3.0" % "test",
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
  "org.mongodb" %% "casbah" % "2.8.1"
)
