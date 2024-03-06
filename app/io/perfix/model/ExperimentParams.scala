package io.perfix.model

import io.perfix.question.QuestionParams

case class ExperimentParams() extends QuestionParams {

  val dataDescription = new DataDescription
  var concurrentQueriesOpt: Option[Int] = None
  var writeBatchSize: Int = 100

  def concurrentQueries: Int = {
    concurrentQueriesOpt.get
  }

  def isDefined: Boolean = {
    concurrentQueriesOpt.isDefined && dataDescription.isDefined
  }
}
