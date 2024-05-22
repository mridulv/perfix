package io.perfix.manager

import com.google.inject.{Inject, Singleton}
import io.perfix.exceptions.InvalidStateException
import io.perfix.model._
import io.perfix.store.DatasetConfigStore


@Singleton
class DatasetManager @Inject()(datasetConfigStore: DatasetConfigStore) {
  private val SAMPLE_ROWS = 100

  def create(datasetParams: DatasetParams): DatasetId = {
    datasetConfigStore.create(datasetParams).id
      .getOrElse(throw InvalidStateException("Invalid DatasetParams"))
  }

  def get(datasetId: DatasetId): DatasetParams = {
    datasetConfigStore.get(datasetId)
      .getOrElse(throw InvalidStateException("Invalid Dataset"))
  }

  def data(datasetId: DatasetId): Dataset = {
    datasetConfigStore.get(datasetId)
      .map(_.dataset.sampleDataset(SAMPLE_ROWS))
      .getOrElse(throw InvalidStateException("Invalid Dataset"))
  }

  def update(datasetId: DatasetId,
             dataset: DatasetParams): DatasetParams = {
    datasetConfigStore.update(datasetId, dataset)
    datasetConfigStore.get(datasetId)
      .getOrElse(throw InvalidStateException("Invalid Dataset"))
  }

  def all(entityFilters: Seq[EntityFilter]): Seq[DatasetParams] = {
    val allDatasets = datasetConfigStore.list()
    allDatasets.filter { dataset =>
      val datasetFilters = entityFilters.collect {
        case df: DatasetFilter => df
      }
      datasetFilters.forall(df => df.filterDataset(dataset.dataset))
    }
  }

  def delete(datasetId: DatasetId): Unit = {
    datasetConfigStore.delete(datasetId)
  }

}
