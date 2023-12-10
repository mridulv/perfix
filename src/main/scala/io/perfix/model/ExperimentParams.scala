package io.perfix.model

import io.perfix.question.QuestionParams

case class ExperimentParams() extends QuestionParams {
  var concurrentQueriesOpt: Option[Int] = None

  def concurrentQueries: Int = {
    concurrentQueriesOpt.get
  }

  def isDefined: Boolean = {
    concurrentQueriesOpt.isDefined
  }
}
