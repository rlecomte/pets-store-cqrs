package io.rlc.pets.http

import scala.concurrent.Promise

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.{RequestContext, RouteResult, _}

import io.rlc.infra.AggregateRef

package object utils {

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

}
