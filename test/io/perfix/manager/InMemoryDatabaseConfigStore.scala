package io.perfix.manager

import io.perfix.model.api.{DatabaseConfigId, DatabaseConfigParams}
import io.perfix.db.DatabaseConfigStore
import org.mockito.Mockito
import play.api.db.slick.DatabaseConfigProvider

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.Random

class InMemoryDatabaseConfigStore extends DatabaseConfigStore(Mockito.mock(classOf[DatabaseConfigProvider]))(ExecutionContext.global) {
  private val configs: mutable.Map[DatabaseConfigId, DatabaseConfigParams] = mutable.Map.empty

  override def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigParams = {
    val id = DatabaseConfigId(Random.nextInt())
    val updatedParams = databaseConfigParams.copy(databaseConfigId = Some(id))
    configs(id) = updatedParams
    updatedParams
  }

  override def get(databaseConfigId: DatabaseConfigId): Option[DatabaseConfigParams] = {
    configs.get(databaseConfigId)
  }

  override def update(databaseConfigId: DatabaseConfigId,
                      databaseConfigParams: DatabaseConfigParams): Int = {
    configs.get(databaseConfigId) match {
      case Some(_) =>
        configs(databaseConfigId) = databaseConfigParams
        1
      case None => 0
    }
  }

  override def delete(databaseConfigId: DatabaseConfigId): Int = {
    configs.remove(databaseConfigId) match {
      case Some(_) => 1
      case None => 0
    }
  }

  override def list(): Seq[DatabaseConfigParams] = {
    configs.values.toSeq
  }
}

