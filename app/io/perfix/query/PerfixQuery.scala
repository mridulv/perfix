package io.perfix.query

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
