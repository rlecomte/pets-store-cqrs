package io.rlc.pets.domain.categories

import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, SnapshotOffer}

import io.rlc.infra.workflow.{Error, OK}
import io.rlc.pets.domain.categories.command.{ArchivePetsCategoryCmd, CreatePetsCategoryCmd, UpdatePetsCategoryCmd}
import io.rlc.pets.domain.categories.event.{PetsCategoryArchived, PetsCategoryCreated, PetsCategoryEvent, PetsCategoryUpdated}
import io.rlc.pets.domain.categories.repository.PetsCategoriesRepo

case class PetsCategoryAggregate(aggregateId: String, repo: PetsCategoriesRepo) extends PersistentActor with ActorLogging {

  var snapShotInterval: Int = 5
  var category: PetsCategory = null

  override def receiveRecover: Receive = {
    case event: PetsCategoryEvent => updateState(event)
    case SnapshotOffer(_, snapshot: PetsCategory) => {
      println(s"offered state = $snapshot")
      category = snapshot
    }
  }

  override def receiveCommand: Receive = {
    case CreatePetsCategoryCmd(name) if category == null => {
      persist(PetsCategoryCreated(name)) { ev =>
        processPersistEvent(ev)
      }
      sender() ! OK(aggregateId)
    }
    case UpdatePetsCategoryCmd(_, name) if category != null && !category.archived => {
      persist(PetsCategoryUpdated(name)) { ev =>
        processPersistEvent(ev)
      }
      sender() ! OK(aggregateId)
    }
    case ArchivePetsCategoryCmd(name) if category != null && !category.archived => {
      persist(PetsCategoryArchived) { ev =>
        processPersistEvent(ev)
      }
      sender() ! OK(aggregateId)
    }
    case _ => {
      println("no way!")
      sender() ! Error("no way!")
    }
  }

  private def processPersistEvent(ev: PetsCategoryEvent) = {
    updateState(ev)
    context.system.eventStream.publish(ev)
    if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0) saveSnapshot(category)
  }

  private def updateState(event: PetsCategoryEvent): Unit = event match {
    case PetsCategoryCreated(name) => {
      category = PetsCategory(persistenceId, name)
      save()
    }
    case PetsCategoryUpdated(name) => {
      category = category.copy(name = name)
      save()
    }
    case PetsCategoryArchived => {
      category = category.copy(archived = true)
      save()
    }
  }

  private def save(): Unit = repo.save(category)

  override def persistenceId: String = s"$aggregateId"
}