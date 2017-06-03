package io.rlc.pets.domain.categories.repository

import io.rlc.pets.domain.categories.PetsCategory

trait PetsCategoriesRepo {

  def save(category: PetsCategory): Unit

  def get(category: PetsCategory): Option[PetsCategory]
}
