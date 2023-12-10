package io.perfix.query

case class PerfixQuery(filtersOpt: Option[List[PerfixQueryFilter]],
                       projectedFieldsOpt: Option[List[String]],
                       limitOpt: Option[Int])
