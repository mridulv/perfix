package io.perfix.query

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SqlDBQueryBuilderFilterTest extends AnyFlatSpec with Matchers {

  "PerfixQueryFilter" should "format string values with quotes" in {
    val filter = DbQueryFilter("name", "John")
    assert(filter.toString == "name = \"John\"")
  }

  "PerfixQueryFilter" should "format numeric values without quotes" in {
    val filter = DbQueryFilter("age", 30)
    assert(filter.toString == "age = 30")
  }

  "PerfixQueryFilter" should "format boolean values without quotes" in {
    val filter = DbQueryFilter("isActive", true)
    assert(filter.toString == "isActive = true")
  }

}
