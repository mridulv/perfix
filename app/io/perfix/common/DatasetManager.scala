package io.perfix.common

import com.google.inject.Inject
import io.perfix.model.{DatasetId, DatasetParams}

import scala.collection.mutable

@Inject
class DatasetManager {
  val mapping: mutable.Map[DatasetId, DatasetParams] = mutable.Map.empty[DatasetId, DatasetParams]

  def create(datasetParams: DatasetParams): DatasetId = ???

  def get(datasetId: DatasetId): DatasetParams = ???

}
