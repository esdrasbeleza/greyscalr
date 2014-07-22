import play.PlayScala

name := """greyscalr"""

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.mongodb" %% "casbah" % "2.7.2",
  "com.amazonaws" % "aws-java-sdk" % "1.8.5"
)
