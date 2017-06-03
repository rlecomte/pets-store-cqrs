package io.rlc

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import io.rlc.pets.http.PetsServer

object Main extends App {
  implicit val system = ActorSystem("PetsSystem")
  implicit val materializer = ActorMaterializer()

  new PetsServer().start()
}
