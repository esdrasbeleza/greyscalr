import play.PlayScala

name := """greyscalr"""

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.reactivemongo" %% "reactivemongo" % "0.11.0-SNAPSHOT",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.0-SNAPSHOT",
  "javax.inject" % "javax.inject" % "1",
  "com.google.inject" % "guice" % "3.0",
  "com.amazonaws" % "aws-java-sdk" % "1.8.5"
)
