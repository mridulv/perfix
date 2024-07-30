package io.perfix.query

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SqlDBQueryBuilderTest extends AnyFlatSpec with Matchers {

  "SqlQueryBuilder" should "handle all query components" in {
    val query = SqlDBQueryBuilder(
      filtersOpt = Some(List(DbQueryFilter("age", 18), DbQueryFilter("name", "John"))),
      projectedFieldsOpt = Some(List("name", "age")),
      tableName = "table"
    )

    val sqlQuery = query.convert
    assert(sqlQuery.sql == "select name, age from table where age = 18 and name = \"John\"")
  }

  "SqlQueryBuilder" should "handle no filters" in {
    val query = SqlDBQueryBuilder(None, Some(List("name", "age")), "table")

    val sqlQuery = query.convert
    assert(sqlQuery.sql == "select name, age from table ")
  }

  "SqlQueryBuilder" should "handle no projected fields" in {
    val query = SqlDBQueryBuilder(Some(List(DbQueryFilter("age", 18))), None, "table")

    val sqlQuery = query.convert
    assert(sqlQuery.sql == "select * from table where age = 18")
  }

  "SqlQueryBuilder" should "handle no limit" in {
    val query = SqlDBQueryBuilder(Some(List(DbQueryFilter("age", 18))), Some(List("name")), "table")

    val sqlQuery = query.convert
    assert(sqlQuery.sql == "select name from table where age = 18")
  }

  "SqlQueryBuilder" should "handle empty query" in {
    val query = SqlDBQueryBuilder(None, None, "table")

    val sqlQuery = query.convert
    assert(sqlQuery.sql == "select * from table ")
  }
}
