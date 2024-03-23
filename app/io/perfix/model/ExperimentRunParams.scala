package io.perfix.model

import io.perfix.query.PerfixQuery
import io.perfix.query.PerfixQuery._
import play.api.libs.json.{Format, Json}

case class ExperimentRunParams(rows: Int,
                               batchSize: Int,
                               benchmarkTimeSeconds: Int,
                               perfixQuery: Option[PerfixQuery] = None)

object ExperimentRunParams {
  implicit val ExperimentRunParams: Format[ExperimentRunParams] = Json.format[ExperimentRunParams]
}
