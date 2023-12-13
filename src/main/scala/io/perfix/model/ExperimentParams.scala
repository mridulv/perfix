package io.perfix.model

import io.perfix.generator.FakeDataGenerator
import io.perfix.question.QuestionParams

case class ExperimentParams() extends QuestionParams {

  private val fakeDataGenerator = new FakeDataGenerator
  val dataDescription = new DataDescription
  var fakeData: DataWithDescription = _
  var concurrentQueriesOpt: Option[Int] = None

  def concurrentQueries: Int = {
    concurrentQueriesOpt.get
  }

  def isDefined: Boolean = {
    concurrentQueriesOpt.isDefined && dataDescription.isDefined
  }

  def experimentData(): DataWithDescription = {
    this.fakeData = fakeDataGenerator.generateData(dataDescription)
    this.fakeData
  }
}
