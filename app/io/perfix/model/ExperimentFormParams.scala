package io.perfix.model

import io.perfix.query.PerfixQuery
import io.perfix.forms.FormParams

case class ExperimentFormParams() extends FormParams {

  val dataDescription = new DataDescription
  var concurrentQueriesOpt: Option[Int] = None
  var writeBatchSize: Int = 100
  var benchmarkTimeSeconds: Int = 15
  var perfixQuery: PerfixQuery = PerfixQuery.empty

  def concurrentQueries: Int = {
    concurrentQueriesOpt.get
  }

  def isDefined: Boolean = {
    concurrentQueriesOpt.isDefined && dataDescription.isDefined
  }
}
