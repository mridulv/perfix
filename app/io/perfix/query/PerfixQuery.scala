package io.perfix.query

import io.perfix.model.PerfixQuestionAnswer
import play.api.libs.json.{Format, Json, Writes}
import PerfixQuestionAnswer._

case class PerfixQuery(filtersOpt: Option[List[PerfixQueryFilter]] = None,
                       projectedFieldsOpt: Option[List[String]] = None,
                       limitOpt: Option[Int] = None) {

  def selectFields: Seq[String] = {
    projectedFieldsOpt.map(_.toSeq).getOrElse(Seq.empty)
  }

  def filterFields: Seq[String] = {
    filtersOpt.map(_.map(_.field).toSeq).getOrElse(Seq.empty)
  }

}

object PerfixQuery {
  implicit val PerfixQueryFormatter: Format[PerfixQuery] = Json.format[PerfixQuery]

  def empty: PerfixQuery = {
    PerfixQuery(limitOpt = Some(1))
  }
}
