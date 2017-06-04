package io.rlc.pets.http

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer

import io.rlc.DomainModelPets
import io.rlc.pets.domain.categories.command.{ArchivePetsCategoryCmd, CreatePetsCategoryCmd}
import io.rlc.pets.http.utils.ResultHandler

object CategoryRouting {

  def routes()(implicit materializer: Materializer, actorSystem: ActorSystem): Route = {
    implicit val ec = actorSystem.dispatcher

    path("category") {
      post {
        ResultHandler.aggregateHandler(DomainModelPets.newCategory(), CreatePetsCategoryCmd("Cats"))
      }
    } ~
      path("category" / Segment / "archive") { aggregateId =>
        put {
          ResultHandler.aggregateHandler(DomainModelPets.fetchCategory(aggregateId), ArchivePetsCategoryCmd(aggregateId))
        }
      }
  }
}
