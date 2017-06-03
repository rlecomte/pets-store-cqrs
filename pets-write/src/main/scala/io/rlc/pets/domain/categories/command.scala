package io.rlc.pets.domain.categories

object command {
  sealed trait PetsCategoriesCmd
  case class CreatePetsCategoryCmd(name: String) extends PetsCategoriesCmd
  case class UpdatePetsCategoryCmd(id: String, name: String) extends PetsCategoriesCmd
  case class ArchivePetsCategoryCmd(id: String) extends PetsCategoriesCmd
}
