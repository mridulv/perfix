package io.perfix.query

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SqlDBQueryBuilderTest extends AnyFlatSpec with Matchers {

  "PerfixQuery" should "hold the correct filter values" in {
    val filters = List(DbQueryFilter("age", 18), DbQueryFilter("name", "John"))
    val query = SqlDBQueryBuilder(Some(filters), None, None)
    assert(query.filtersOpt.contains(filters))
  }

  "PerfixQuery" should "hold the correct projected fields" in {
    val projectedFields = List("name", "age")
    val query = SqlDBQueryBuilder(None, Some(projectedFields), None)
    assert(query.projectedFieldsOpt.contains(projectedFields))
  }

  "PerfixQuery" should "hold the correct limit value" in {
    val query = SqlDBQueryBuilder(None, None, Some(10))
    assert(query.limitOpt.contains(10))
  }
}
