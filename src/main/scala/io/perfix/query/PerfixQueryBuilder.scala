package io.perfix.query

class PerfixQueryBuilder {
  private var filters: List[PerfixQueryFilter] = List()
  private var projectedFields: List[String] = List()
  private var limit: Option[Int] = None

  def withFilter(filter: PerfixQueryFilter): PerfixQueryBuilder = {
    filters = filters :+ filter
    this
  }

  def withProjectedField(field: String): PerfixQueryBuilder = {
    projectedFields = projectedFields :+ field
    this
  }

  def withLimit(lim: Int): PerfixQueryBuilder = {
    limit = Some(lim)
    this
  }

  def build(): PerfixQuery = {
    PerfixQuery(
      Option(filters).filter(_.nonEmpty),
      Option(projectedFields).filter(_.nonEmpty),
      limit
    )
  }
}
