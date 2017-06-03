package io.rlc.pets.domain.categories

import io.rlc.infra.DomainEvent

object event {
  sealed trait PetsCategoryEvent extends DomainEvent
  case class PetsCategoryCreated(aggregateId: String, name: String) extends PetsCategoryEvent
  case class PetsCategoryUpdated(aggregateId: String, name: String) extends PetsCategoryEvent
  case class PetsCategoryArchived(aggregateId: String) extends PetsCategoryEvent
}
