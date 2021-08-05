name := "authorizer"

version := "0.1"

scalaVersion := "2.13.0"
val AkkaVersion = "2.6.15"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "org.scalatest" %% "scalatest" % "3.2.0" % Test,
  "org.scalatest" %% "scalatest-wordspec" % "3.2.0" % "test",
  "org.scalatestplus" %% "scalatestplus-mockito" % "1.0.0-M2" % Test
)

addCommandAlias("check", ";clean;cleanFiles;test:compile;coverage;test;coverageReport;scalastyle")