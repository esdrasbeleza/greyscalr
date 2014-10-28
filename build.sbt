import play.PlayScala
import sbtfindtags.FindtagsPlugin._

name := """greyscalr"""

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

findtagsTags := Seq("TODO", "FIXME", "IMPROVE")

//findtagsFailsIfTagsAreFound := true

compile <<= (compile in Compile) dependsOn findtags

scalaVersion := "2.11.2"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  "javax.inject" % "javax.inject" % "1",
  "com.google.inject" % "guice" % "3.0",
  "com.amazonaws" % "aws-java-sdk" % "1.8.5"
)
