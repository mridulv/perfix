package io.perfix.query

import io.perfix.stores.mysql.MySQLStore.convert
import io.perfix.stores.mysql.MySQLTableParams
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PerfixQueryConverterTest extends AnyFlatSpec with Matchers {

  "PerfixQueryConverter" should "handle all query components" in {
    val tableParams = MySQLTableParams("testDB", "testTable")
    val query = PerfixQuery(
      filtersOpt = Some(List(PerfixQueryFilter("age", 18), PerfixQueryFilter("name", "John"))),
      projectedFieldsOpt = Some(List("name", "age")),
      limitOpt = Some(10)
    )

    val sqlQuery = convert(tableParams, query)
    assert(sqlQuery == "select name, age from testDB.testTable where age = 18 and name = \"John\" limit 10")
  }

  "PerfixQueryConverter" should "handle no filters" in {
    val tableParams = MySQLTableParams("testDB", "testTable")
    val query = PerfixQuery(None, Some(List("name", "age")), Some(10))

    val sqlQuery = convert(tableParams, query)
    assert(sqlQuery == "select name, age from testDB.testTable  limit 10")
  }

  "PerfixQueryConverter" should "handle no projected fields" in {
    val tableParams = MySQLTableParams("testDB", "testTable")
    val query = PerfixQuery(Some(List(PerfixQueryFilter("age", 18))), None, None)

    val sqlQuery = convert(tableParams, query)
    assert(sqlQuery == "select * from testDB.testTable where age = 18 ")
  }

  "PerfixQueryConverter" should "handle no limit" in {
    val tableParams = MySQLTableParams("testDB", "testTable")
    val query = PerfixQuery(Some(List(PerfixQueryFilter("age", 18))), Some(List("name")), None)

    val sqlQuery = convert(tableParams, query)
    assert(sqlQuery == "select name from testDB.testTable where age = 18 ")
  }

  "PerfixQueryConverter" should "handle empty query" in {
    val tableParams = MySQLTableParams("testDB", "testTable")
    val query = PerfixQuery(None, None, None)

    val sqlQuery = convert(tableParams, query)
    assert(sqlQuery == "select * from testDB.testTable  ")
  }
}
