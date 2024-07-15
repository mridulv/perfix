package io.perfix.query

case class NoSqlDBQuery(filtersOpt: Option[List[DbQueryFilter]] = None) extends DBQuery
