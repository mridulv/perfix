package io.perfix.query

case class PerfixQuery(filtersOpt: Option[List[PerfixQueryFilter]] = None,
                       projectedFieldsOpt: Option[List[String]] = None,
                       limitOpt: Option[Int] = None)
