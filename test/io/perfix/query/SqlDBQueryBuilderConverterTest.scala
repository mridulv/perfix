package io.perfix.query

import io.perfix.stores.mysql.MySQLStore.convert
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SqlDBQueryBuilderConverterTest extends AnyFlatSpec with Matchers {

  "PerfixQueryConverter" should "handle all query components" in {
    val query = SqlDBQueryBuilder(
      filtersOpt = Some(List(DbQueryFilter("age", 18), DbQueryFilter("name", "John"))),
      projectedFieldsOpt = Some(List("name", "age")),
      limitOpt = Some(10)
    )

    val sqlQuery = convert("testDB", "testTable", query)
    assert(sqlQuery == "select name, age from testDB.testTable where age = 18 and name = \"John\" limit 10")
  }

  "PerfixQueryConverter" should "handle no filters" in {
    val query = SqlDBQueryBuilder(None, Some(List("name", "age")), Some(10))

    val sqlQuery = convert("testDB", "testTable", query)
    assert(sqlQuery == "select name, age from testDB.testTable  limit 10")
  }

  "PerfixQueryConverter" should "handle no projected fields" in {
    val query = SqlDBQueryBuilder(Some(List(DbQueryFilter("age", 18))), None, None)

    val sqlQuery = convert("testDB", "testTable", query)
    assert(sqlQuery == "select * from testDB.testTable where age = 18 ")
  }

  "PerfixQueryConverter" should "handle no limit" in {
    val query = SqlDBQueryBuilder(Some(List(DbQueryFilter("age", 18))), Some(List("name")), None)

    val sqlQuery = convert("testDB", "testTable", query)
    assert(sqlQuery == "select name from testDB.testTable where age = 18 ")
  }

  "PerfixQueryConverter" should "handle empty query" in {
    val query = SqlDBQueryBuilder(None, None, None)

    val sqlQuery = convert("testDB", "testTable", query)
    assert(sqlQuery == "select * from testDB.testTable  ")
  }
}
