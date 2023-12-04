package io.perfix.generator

import io.perfix.model.{DataDescription, DataWithDescription}

trait DataGenerator {

  def generateData(dataDescription: DataDescription): DataWithDescription

}
