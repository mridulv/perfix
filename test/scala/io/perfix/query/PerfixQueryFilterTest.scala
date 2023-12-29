package io.perfix.query

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PerfixQueryFilterTest extends AnyFlatSpec with Matchers {

  "PerfixQueryFilter" should "format string values with quotes" in {
    val filter = PerfixQueryFilter("name", "John")
    assert(filter.toString == "name = \"John\"")
  }

  "PerfixQueryFilter" should "format numeric values without quotes" in {
    val filter = PerfixQueryFilter("age", 30)
    assert(filter.toString == "age = 30")
  }

  "PerfixQueryFilter" should "format boolean values without quotes" in {
    val filter = PerfixQueryFilter("isActive", true)
    assert(filter.toString == "isActive = true")
  }

}
