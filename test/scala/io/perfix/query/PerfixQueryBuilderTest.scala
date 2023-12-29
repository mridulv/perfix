package io.perfix.query

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PerfixQueryBuilderTest extends AnyFlatSpec with Matchers  {

  "PerfixQueryBuilder" should "add filters correctly" in {
    val builder = new PerfixQueryBuilder().withFilter(PerfixQueryFilter("age", 18))
    val query = builder.build()
    assert(query.filtersOpt.contains(List(PerfixQueryFilter("age", 18))))
  }

  "PerfixQueryBuilder" should "add projected fields correctly" in {
      val builder = new PerfixQueryBuilder().withProjectedField("name")
      val query = builder.build()
      assert(query.projectedFieldsOpt.contains(List("name")))
  }

  "PerfixQueryBuilder" should "set limit correctly" in {
      val builder = new PerfixQueryBuilder().withLimit(10)
      val query = builder.build()
      assert(query.limitOpt.contains(10))
  }

}
