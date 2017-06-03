package io.rlc.infra

import scala.reflect.ClassTag

import akka.actor.{ActorSystem, Props}

object DomainModel {
  def apply(name: String): DomainModel = {
    new DomainModel(name)
  }
}

class DomainModel(name: String) {
  val aggregateTypeRegistry = scala.collection.mutable.Map[String, AggregateType]()
  val system = ActorSystem(name)

  def aggregateOf[T](id: String)(implicit classTag: ClassTag[T]): AggregateRef = {
    val typeName = classTag.toString()
    if (aggregateTypeRegistry.contains(typeName)) {
      val aggregateType = aggregateTypeRegistry(typeName)
      aggregateType.cacheActor ! RegisterAggregateId(id)
      AggregateRef(id, aggregateType.cacheActor)
    } else {
      throw new IllegalStateException(s"DomainModel type registry does not have a $typeName")
    }
  }

  def registerAggregateType[T](implicit aggregateProps: AggregateProps[T], classTag: ClassTag[T]): Unit = {
    val typeName = classTag.toString()
    if (!aggregateTypeRegistry.contains(typeName)) {
      val actorRef = system.actorOf(Props(new AggregateCache(typeName)), typeName)
      aggregateTypeRegistry(typeName) = AggregateType(actorRef)
    }
  }

  def shutdown() = {
    system.terminate()
  }
}