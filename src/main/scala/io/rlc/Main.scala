package io.rlc

import java.io.File

import akka.actor.ActorSystem
import akka.persistence.cassandra.testkit.CassandraLauncher
import akka.stream.ActorMaterializer

import io.rlc.pets.http.PetsServer

object Main extends App {
  implicit val system = ActorSystem("PetsSystem")
  implicit val materializer = ActorMaterializer()

  CassandraLauncher.start(
    new File(".cassandra"),
    CassandraLauncher.DefaultTestConfigResource,
    clean = true,
    port = 9042,
    CassandraLauncher.classpathForResources("logback.xml")
  )

  new PetsServer().start()
}
