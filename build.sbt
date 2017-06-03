import sbt._

name := "pets-store-cqrs"

lazy val akkaVersion = "2.5.2"
val akkaHttpV = "10.0.7"

val GlobalSettings = Seq(
  version := "1.0",
  scalaVersion := "2.12.2"
)

val dependencies = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
    "com.chuusai" %% "shapeless" % "2.3.2",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "io.monix" %% "monix" % "2.3.0",
    "org.typelevel" %% "cats" % "0.9.0",
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % "test"
  ),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3"),
  libraryDependencies += "com.github.dnvriend" %% "akka-persistence-inmemory" % "2.5.1.1"
)

cancelable in Global := true
fork in run := true

lazy val root = (project in file("."))
  .dependsOn(`core`, `pets-write`, `pets-read`)
  .aggregate(`core`, `pets-write`, `pets-read`)
  .settings(GlobalSettings)

lazy val `core` = (project in file("core"))
  .settings(GlobalSettings)
  .settings(dependencies)

lazy val `pets-write` = (project in file("pets-write"))
  .dependsOn(`core`)
  .settings(GlobalSettings)
  .settings(dependencies)

lazy val `pets-read` = (project in file("pets-read"))
  .dependsOn(`core`)
  .settings(GlobalSettings)
  .settings(dependencies)