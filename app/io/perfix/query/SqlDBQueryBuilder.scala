package io.perfix.query

import play.api.libs.json.{Format, Json}

case class SqlDBQueryBuilder(filtersOpt: Option[List[DbQueryFilter]] = None,
                             projectedFieldsOpt: Option[List[String]] = None,
                             tableName: String) extends DBQuery {

  def convert: SqlDBQuery = {
    val sqlFilterPart = filtersOpt match {
      case Some(filters) => "where " + filters.map(_.toString).mkString(" and ")
      case None => ""
    }

    val projectedFieldsPart = projectedFieldsOpt match {
      case Some(projectedFields) => projectedFields.mkString(", ")
      case None => "*"
    }

    SqlDBQuery(s"select $projectedFieldsPart from $tableName ${sqlFilterPart}")
  }

}

object SqlDBQueryBuilder {
  implicit val SqlDBQueryBuilderFormatter: Format[SqlDBQueryBuilder] = Json.format[SqlDBQueryBuilder]
}
