package io.perfix.generator

import io.perfix.model.{Dataset, DatasetParams}

trait DataGenerator {

  def generateData(datasetParams: DatasetParams): Dataset

}
