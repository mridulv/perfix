package io.perfix.question.mysql

import TableParamsQuestions._
import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{DataType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.dynamodb.DynamoDBConnectionParametersQuestions.{ACCESS_ID, ACCESS_SECRET, CONNECTION_URL}
import io.perfix.stores.mysql.{MySQLParams, MySQLTableParams}

class TableParamsQuestions(override val storeQuestionParams: MySQLParams) extends Question {

  override val mapping: Map[QuestionLabel, DataType] = Map(
    DBNAME -> StringType,
    TABLENAME -> StringType
  )

  override def shouldAsk(): Boolean = {
    storeQuestionParams.mySQLTableParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import storeQuestionParams._
    mySQLTableParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLTableParams")
      case None => mySQLTableParams = Some(MySQLTableParams(answers(DBNAME).toString, answers(TABLENAME).toString))
    }
  }

}

object TableParamsQuestions {
  val DBNAME = "dbName"
  val TABLENAME = "tableName"

  def apply(mySQLParams: MySQLParams): TableParamsQuestions = {
    new TableParamsQuestions(mySQLParams)
  }
}
