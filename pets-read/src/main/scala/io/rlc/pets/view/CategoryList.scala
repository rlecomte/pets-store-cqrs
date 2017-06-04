package io.rlc.pets.view

import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.{EventEnvelope, Offset, PersistenceQuery}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

import io.rlc.pets.domain.categories.event.{PetsCategoryArchived, PetsCategoryCreated, PetsCategoryEvent, PetsCategoryUpdated}

class CategoryList extends PersistentActor {

  //var offset        = 0L

  val categories = scala.collection.mutable.HashMap.empty[String,String]
  override val persistenceId: String = "CategoryListView"

  override def receiveRecover: Receive = {
    // Recovery from snapshot will always give us last sequence number
    //case SnapshotOffer(meta, _) => offset = meta.sequenceNr
    case RecoveryCompleted => recoveryCompleted()
  }

  def recoveryCompleted() = {
    implicit val materializer = ActorMaterializer()
    val queries = PersistenceQuery(context.system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)
    queries.eventsByTag("PetsCategoryEvent", Offset.noOffset)
      .collect { case envelope@EventEnvelope(_, id, _, ev: PetsCategoryEvent) => (id, ev) }
      .to(Sink.actorRef(self, ()))
  }

  override def receiveCommand: Receive = {
    case (id:String, PetsCategoryCreated(name)) => categories += (id -> name); show()
    case (id:String, PetsCategoryUpdated(name)) => categories += (id -> name); show()
    case (id:String, PetsCategoryArchived) => categories.remove(id); show()
  }

  def show() = {
    println("Category view updated!")
    println(categories.toList.toString())
  }
}
