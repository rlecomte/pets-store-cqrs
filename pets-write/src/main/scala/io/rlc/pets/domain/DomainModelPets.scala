package io.rlc.pets.domain

import io.rlc.infra.DomainModel
import io.rlc.infra.utils.UUID
import io.rlc.pets.domain.categories.PetsCategoryAggregate
import scala.reflect._

object DomainModelPets {

  val model = DomainModel("DomainModelPetsSystem")
  model.registerAggregateType[PetsCategoryAggregate]

  def newCategory() = {
    model.aggregateOf[PetsCategoryAggregate](UUID.next().toString)
  }

  def fetchCategory(id: String) = {
    model.aggregateOf[PetsCategoryAggregate](id)
  }
}
