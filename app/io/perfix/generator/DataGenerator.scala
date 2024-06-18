package io.perfix.generator

import io.perfix.model.api.{Dataset, DatasetParams}

trait DataGenerator {

  def generateData(datasetParams: DatasetParams): Dataset

}
