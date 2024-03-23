package io.perfix.question.mysql

import TableParamsQuestions._
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.stores.mysql.{MySQLParams, MySQLTableParams}

class TableParamsQuestions(override val storeQuestionParams: MySQLParams) extends Question {

  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    DBNAME -> QuestionType(StringType),
    TABLENAME -> QuestionType(StringType)
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
