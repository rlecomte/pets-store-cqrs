package io.rlc

import scala.concurrent.Promise
import scala.io.StdIn

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}

import io.rlc.infra.AggregateRef
import io.rlc.pets.domain.DomainModelPets
import io.rlc.pets.domain.categories.command.{ArchivePetsCategoryCmd, CreatePetsCategoryCmd}

object Main extends App {

  {
    implicit val system = ActorSystem("PetsSystem")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route = {
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
    val categoryAggregate = DomainModelPets.newCategory()
    categoryAggregate ! CreatePetsCategoryCmd("Cats")

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    //StdIn.readLine() // let it run until user presses return
    //bindingFuture
    //  .flatMap(_.unbind()) // trigger unbinding from the port
    //  .onComplete(_ => system.terminate()) // and shutdown when done
  }
}

case class ResultHandler(requestCtx: ImperativeRequestContext) extends Actor {

  import io.rlc.infra.workflow._

  override def receive: Receive = {
    case OK(id) => requestCtx.complete(s"$id")
    case Error(message) => requestCtx.fail(new IllegalArgumentException(message))
  }
}
// an imperative wrapper for request context
case class ImperativeRequestContext(ctx: RequestContext, promise: Promise[RouteResult] = Promise[RouteResult]()) {
  private implicit val ec = ctx.executionContext
  def complete(obj: ToResponseMarshallable): Unit = ctx.complete(obj).onComplete(promise.complete)
  def fail(error: Throwable): Unit = ctx.fail(error).onComplete(promise.complete)
}

object ResultHandler {
  def aggregateHandler[T](aggregateRef: AggregateRef, cmd: T)(implicit system: ActorSystem): Route = { ctx: RequestContext =>
    val irc = ImperativeRequestContext(ctx)
    val handler = system.actorOf(Props(classOf[ResultHandler], irc))
    aggregateRef.tell(cmd)(handler)
    irc.promise.future
  }
}
