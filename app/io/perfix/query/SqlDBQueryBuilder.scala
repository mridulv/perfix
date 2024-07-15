package io.perfix.query

import play.api.libs.json.{Format, Json}

case class SqlDBQueryBuilder(filtersOpt: Option[List[DbQueryFilter]] = None,
                             projectedFieldsOpt: Option[List[String]] = None,
                             limitOpt: Option[Int] = None) extends DBQuery {

  def selectFields: Seq[String] = {
    projectedFieldsOpt.map(_.toSeq).getOrElse(Seq.empty)
  }

  def filterFields: Seq[String] = {
    filtersOpt.map(_.map(_.field).toSeq).getOrElse(Seq.empty)
  }

}

object SqlDBQueryBuilder {
  implicit val SqlDBQueryBuilderFormatter: Format[SqlDBQueryBuilder] = Json.format[SqlDBQueryBuilder]

  def empty: SqlDBQueryBuilder = {
    SqlDBQueryBuilder(limitOpt = Some(1))
  }
}
