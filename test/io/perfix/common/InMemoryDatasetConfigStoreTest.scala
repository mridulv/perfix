package io.perfix.common

import io.perfix.model.{DatasetId, DatasetParams}
import io.perfix.store.DatasetConfigStore
import org.mockito.Mockito

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.util.Random

class InMemoryDatasetConfigStore extends DatasetConfigStore(Mockito.mock(classOf[DatabaseConfigProvider]))(ExecutionContext.global)  {
  private val inMemoryStore: mutable.Map[DatasetId, DatasetParams] = mutable.Map.empty
  override def create(datasetParams: DatasetParams): DatasetParams = {
    val newId = DatasetId(Random.nextInt())  // Generate a new unique ID
    val updatedParams = datasetParams.copy(id = Some(newId))
    inMemoryStore(newId) = updatedParams
    updatedParams
  }

  override def get(datasetId: DatasetId): Option[DatasetParams] = {
    inMemoryStore.get(datasetId)
  }

  override def update(datasetId: DatasetId, datasetParams: DatasetParams): Int = {
    inMemoryStore.get(datasetId) match {
      case Some(_) =>
        inMemoryStore(datasetId) = datasetParams
        1  // Mimic successful update
      case None =>
        0  // No record found to update
    }
  }

  override def delete(datasetId: DatasetId): Int = {
    inMemoryStore.remove(datasetId) match {
      case Some(_) => 1
      case None => 0
    }
  }

  override def list(): Seq[DatasetParams] = {
    inMemoryStore.values.toSeq
  }
}
