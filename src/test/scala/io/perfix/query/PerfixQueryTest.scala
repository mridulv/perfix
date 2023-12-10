package io.perfix.query

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PerfixQueryTest extends AnyFlatSpec with Matchers {

  "PerfixQuery" should "hold the correct filter values" in {
    val filters = List(PerfixQueryFilter("age", 18), PerfixQueryFilter("name", "John"))
    val query = PerfixQuery(Some(filters), None, None)
    assert(query.filtersOpt.contains(filters))
  }

  "PerfixQuery" should "hold the correct projected fields" in {
    val projectedFields = List("name", "age")
    val query = PerfixQuery(None, Some(projectedFields), None)
    assert(query.projectedFieldsOpt.contains(projectedFields))
  }

  "PerfixQuery" should "hold the correct limit value" in {
    val query = PerfixQuery(None, None, Some(10))
    assert(query.limitOpt.contains(10))
  }
}
