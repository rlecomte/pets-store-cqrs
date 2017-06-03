package io.rlc.pets.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer

class PetsServer()(implicit materializer: Materializer, actorSystem: ActorSystem) {

  val routes = CategoryRouting.routes()

  def start()= {
    Http().bindAndHandle(routes, "localhost", 8080)
  }
}
