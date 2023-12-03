package io.perfix.generator

import io.perfix.model.DataDescription

trait DataGenerator {

  def generateData(dataDescription: DataDescription): Seq[Map[String, Any]]

}
