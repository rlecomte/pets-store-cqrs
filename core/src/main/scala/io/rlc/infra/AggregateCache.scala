package io.rlc.infra

import akka.actor.Actor

  class AggregateCache[T](typeName: String)(implicit aggregateProps: AggregateProps[T]) extends Actor {
  val aggregateIds = scala.collection.mutable.Set[String]()

  def receive = {
    case message: CacheMessage =>
      val aggregate = context.child(message.id).getOrElse {
        if (!aggregateIds.contains(message.id)) {
          throw new IllegalStateException(s"No aggregate of type $typeName and id ${message.id}")
        } else {
          context.actorOf(aggregateProps.props(message.id))
        }
      }
      aggregate.tell(message.actualMessage, message.sender)

    case register: RegisterAggregateId =>
      this.aggregateIds.add(register.id)
  }
}