package io.perfix.stores.mysql

import io.perfix.query.PerfixQuery

object PerfixQueryConverter {

  type SQLQuery = String

  def convert(mySQLTableParams: MySQLTableParams, perfixQuery: PerfixQuery): String = {
    import perfixQuery._
    val sqlFilterPart = filtersOpt match {
      case Some(filters) => "where " + filters.map(_.toString).mkString(" and ")
      case None => ""
    }

    val projectedFieldsPart = projectedFieldsOpt match {
      case Some(projectedFields) => projectedFields.mkString(", ")
      case None => "*"
    }

    val limitPart = limitOpt match {
      case Some(limit) => s"limit $limit"
      case None => ""
    }

    s"select $projectedFieldsPart from ${mySQLTableParams.dbName}.${mySQLTableParams.tableName} ${sqlFilterPart} ${limitPart}"
  }

}
