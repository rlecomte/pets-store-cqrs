package io.rlc.pets.domain

import akka.actor.Props

import io.rlc.infra.AggregateProps
import io.rlc.pets.domain.categories.repository.InMemPetsCategoriesRepo

package object categories {

  implicit val categoriesProps = new AggregateProps[PetsCategoryAggregate] {
    override def props(id: String): Props = Props(PetsCategoryAggregate(id, InMemPetsCategoriesRepo))
  }
}
