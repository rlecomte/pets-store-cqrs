package io.rlc

import akka.actor.ActorRef

package object infra {

  case class AggregateRef(id: String, cache: ActorRef) {
    def tell(message: Any)(implicit sender: ActorRef = null): Unit = {
      cache ! CacheMessage(id, message, sender)
    }

    def !(message: Any)(implicit sender: ActorRef = null): Unit = {
      cache ! CacheMessage(id, message, sender)
    }
  }

  case class AggregateType(cacheActor: ActorRef)

  case class CacheMessage(id: String, actualMessage: Any, sender: ActorRef)

  case class RegisterAggregateId(id: String)
}
