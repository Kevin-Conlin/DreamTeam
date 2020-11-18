name := "streamer"

version := "0.1"

scalaVersion := "2.12.10"
libraryDependencies ++= Seq(
  "org.twitter4j" % "twitter4j-core" % "4.0.7",
  "org.twitter4j" % "twitter4j-stream" % "4.0.7"
)