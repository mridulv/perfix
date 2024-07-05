package io.perfix.manager

import com.google.inject.{Inject, Singleton}
import io.perfix.exceptions.InvalidStateException
import io.perfix.model._
import io.perfix.model.api.{Dataset, DatasetId, DatasetParams, Datasets}
import io.perfix.db.DatasetConfigStore


@Singleton
class DatasetManager @Inject()(datasetConfigStore: DatasetConfigStore) {
  private val SAMPLE_ROWS = 100

  def create(datasetParams: DatasetParams): DatasetId = {
    val updatedDatasetTableParams = datasetParams
      .getDatasetTableParams
      .zipWithIndex
      .map { case (params, index) =>
        params.copy(tableName = Some(params.tableName.getOrElse(s"table_${index}")))
      }
    datasetConfigStore
      .create(datasetParams.copy(datasetTableParams = Some(updatedDatasetTableParams)))
      .id
      .getOrElse(throw InvalidStateException("Invalid DatasetParams"))
  }

  def get(datasetId: DatasetId): DatasetParams = {
    datasetConfigStore.get(datasetId)
      .getOrElse(throw InvalidStateException("Invalid Dataset"))
  }

  def columns(datasetId: DatasetId): Map[String, Seq[String]] = {
    datasetConfigStore.get(datasetId)
      .getOrElse(throw InvalidStateException("Invalid Dataset"))
      .getDatasetTableParams
      .zipWithIndex
      .map(e => e._1.tableName.getOrElse(s"table_${e._2}") -> e._1.columns.map(_.columnName))
      .toMap
  }

  def data(datasetId: DatasetId): Datasets = {
    val datasets = datasetConfigStore.get(datasetId)
      .map { params =>
        params.datasets.sampleDataset(SAMPLE_ROWS)
          .datasets
          .zipWithIndex
          .map { case (dataset, index) =>
            dataset.copy(tableName = Some(dataset.tableName.getOrElse(s"table_${index}")))
          }
      }.getOrElse(throw InvalidStateException("Invalid Dataset"))
    Datasets(datasets ++ datasets)
  }

  def update(datasetId: DatasetId,
             dataset: DatasetParams): DatasetParams = {
    datasetConfigStore.update(datasetId, dataset)
    datasetConfigStore.get(datasetId)
      .getOrElse(throw InvalidStateException("Invalid Dataset"))
  }

  def all(entityFilters: Seq[EntityFilter]): Seq[DatasetParams] = {
    val allDatasets = datasetConfigStore.list()
    val datasetFilters = entityFilters.collect {
      case df: DatasetFilter => df
    }
    datasetFilters.foldLeft(allDatasets) { (datasets, entityFilter) =>
      datasets.filter(d => entityFilter.filter(d))
    }
  }

  def delete(datasetId: DatasetId): Unit = {
    datasetConfigStore.delete(datasetId)
  }

}
