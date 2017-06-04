package io.rlc.infra

import akka.actor.ExtendedActorSystem
import akka.persistence.journal.{Tagged, WriteEventAdapter}

class TaggingEventsAdapter(system: ExtendedActorSystem) extends WriteEventAdapter {
  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = event match {
    case e: DomainEvent => {
      println("tag event with tag " + e.tag )
      Tagged(event, Set(e.tag))
    }
  }
}
