package io.rlc.pets.domain.categories

import io.rlc.infra.DomainEvent

object event {
  sealed trait PetsCategoryEvent extends DomainEvent {
    val tag = "PetsCategoryEvent"
  }
  case class PetsCategoryCreated(name: String) extends PetsCategoryEvent
  case class PetsCategoryUpdated(name: String) extends PetsCategoryEvent
  case object PetsCategoryArchived extends PetsCategoryEvent
}
