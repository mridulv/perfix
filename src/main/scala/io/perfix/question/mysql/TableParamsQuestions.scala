package io.perfix.question.mysql

import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.StringType
import io.perfix.question.Question
import io.perfix.question.mysql.TableParamsQuestions._
import io.perfix.stores.mysql.{MySQLParams, MySQLTableParams}

class TableParamsQuestions(override val storeQuestionParams: MySQLParams,
                           override val questionExecutionContext: QuestionExecutionContext) extends Question {

  override def shouldAsk(): Boolean = {
    storeQuestionParams.mySQLTableParams.isEmpty
  }

  override def evaluateQuestion(): Unit = {
    import storeQuestionParams._
    mySQLTableParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLTableParams")
      case None =>
        val questionMapping = Map(
          DBNAME -> StringType,
          TABLENAME -> StringType
        )
        val answers = askQuestions(questionMapping)
        MySQLTableParams(answers(DBNAME).toString, answers(TABLENAME).toString)
    }
  }

}

object TableParamsQuestions {
  private val DBNAME = "dbName"
  private val TABLENAME = "tableName"

  def apply(mySQLParams: MySQLParams,
            questionExecutionContext: QuestionExecutionContext): TableParamsQuestions = {
    new TableParamsQuestions(mySQLParams, questionExecutionContext)
  }
}
