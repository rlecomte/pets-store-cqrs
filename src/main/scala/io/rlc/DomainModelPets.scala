package io.rlc

import akka.actor.Props

import io.rlc.infra.DomainModel
import io.rlc.infra.utils.UUID
import io.rlc.pets.domain.categories.PetsCategoryAggregate
import io.rlc.pets.view.CategoryList

object DomainModelPets {

  val model = DomainModel("DomainModelPetsSystem")
  model.registerAggregateType[PetsCategoryAggregate]

  val view = model.actor(Props(new CategoryList))

  def newCategory() = {
    model.aggregateOf[PetsCategoryAggregate](UUID.next().toString)
  }

  def fetchCategory(id: String) = {
    model.aggregateOf[PetsCategoryAggregate](id)
  }
}
