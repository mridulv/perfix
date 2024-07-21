package io.perfix.query

import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.util.TablesNamesFinder
import net.sf.jsqlparser.statement.select.SelectBody
import net.sf.jsqlparser.statement.select.SelectItem
import net.sf.jsqlparser.statement.select.SelectExpressionItem
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.expression.operators.relational.EqualsTo
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.Statement
import play.api.libs.json.{Format, Json}

case class SqlDBQuery(sql: String) extends DBQuery {

  def convert(tableNames: Seq[String], dbName: String): String = {
    tableNames.foldLeft(sql) { case (sql, tableName) =>
      sql.replace(tableName, s"$dbName.$tableName")
    }
  }

  def resolve(mapping: Map[String, Any]): SqlDBQuery = {
    val resolvedSql = mapping.foldLeft(sql) {
      case (resolvedSql, (placeholder, value)) =>
        resolvedSql.replace(s"{{$placeholder}}", value.toString)
    }
    SqlDBQuery(resolvedSql)
  }

  def extractFilters: List[DbQueryFilter] = {
    val statement: Statement = CCJSqlParserUtil.parse(sql)
    val selectStatement: Select = statement.asInstanceOf[Select]
    val selectBody: PlainSelect = selectStatement.getSelectBody.asInstanceOf[PlainSelect]
    val where: Expression = selectBody.getWhere

    def extractFilterExpressions(expression: Expression): List[DbQueryFilter] = {
      expression match {
        case binaryExpression: BinaryExpression =>
          val leftExpression = binaryExpression.getLeftExpression
          val rightExpression = binaryExpression.getRightExpression

          val leftFilter = leftExpression match {
            case col: Column => Some(DbQueryFilter(col.getColumnName, rightExpression.toString))
            case _ => None
          }

          leftFilter.toList ++
            (if (leftExpression.isInstanceOf[BinaryExpression]) extractFilterExpressions(leftExpression.asInstanceOf[BinaryExpression]) else Nil) ++
            (if (rightExpression.isInstanceOf[BinaryExpression]) extractFilterExpressions(rightExpression.asInstanceOf[BinaryExpression]) else Nil)
        case _ => Nil
      }
    }

    if (where != null) extractFilterExpressions(where) else Nil
  }

  def toNoSqlDBQuery: NoSqlDBQuery = {
    NoSqlDBQuery(extractFilters)
  }
}

object SqlDBQuery {
  implicit val SqlDBQueryFormatter: Format[SqlDBQuery] = Json.format[SqlDBQuery]
}
