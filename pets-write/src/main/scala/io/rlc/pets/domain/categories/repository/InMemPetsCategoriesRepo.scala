package io.rlc.pets.domain.categories.repository

import io.rlc.pets.domain.categories.PetsCategory

object InMemPetsCategoriesRepo extends PetsCategoriesRepo {

  private var store: Map[String, PetsCategory] = Map()

  def save(category: PetsCategory): Unit = {
    println(s"register $category")
    store = store.updated(category.id, category)
  }

  def get(category: PetsCategory): Option[PetsCategory] = {
    store.get(category.id)
  }
}
