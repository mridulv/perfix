package io.perfix.model

import io.perfix.question.QuestionParams

case class ExperimentParams() extends QuestionParams {

  val dataDescription = new DataDescription
  var concurrentQueriesOpt: Option[Int] = None

  def concurrentQueries: Int = {
    concurrentQueriesOpt.get
  }

  def isDefined: Boolean = {
    concurrentQueriesOpt.isDefined && dataDescription.isDefined
  }
}
