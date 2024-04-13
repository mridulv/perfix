package io.perfix.manager

import com.google.inject.Inject
import io.perfix.model.{Dataset, DatasetId, DatasetParams}

import scala.collection.mutable
import scala.util.Random

@Inject
class DatasetManager {
  private val SAMPLE_ROWS = 100
  private val mapping: mutable.Map[DatasetId, DatasetParams] = mutable.Map.empty[DatasetId, DatasetParams]

  def create(datasetParams: DatasetParams): DatasetId = {
    val id: Int = Random.nextInt()
    mapping.put(
      DatasetId(id),
      datasetParams.copy(id = Some(DatasetId(id)))
    )
    DatasetId(id)
  }

  def get(datasetId: DatasetId): Dataset = {
    mapping(datasetId).dataset.sampleDataset(SAMPLE_ROWS)
  }

  def update(datasetId: DatasetId,
             dataset: DatasetParams): DatasetId = {
    mapping.put(datasetId, dataset.copy(id = Some(datasetId)))
    datasetId
  }

  def all(): Seq[DatasetParams] = {
    mapping.values.toSeq
  }

}
