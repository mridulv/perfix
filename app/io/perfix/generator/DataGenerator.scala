package io.perfix.generator

import io.perfix.model.api.{Dataset, DatasetParams, Datasets}

trait DataGenerator {

  def generateData(datasetParams: DatasetParams): Datasets

}
