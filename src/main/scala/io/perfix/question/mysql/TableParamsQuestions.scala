package io.perfix.question.mysql

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.StringType
import io.perfix.question.Question
import io.perfix.stores.mysql.{MySQLParams, MySQLTableParams}

class TableParamsQuestions(override val storeQuestionParams: MySQLParams) extends Question {

  private val DBNAME = "dbName"
  private val TABLENAME = "tableName"

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
  def apply(mySQLParams: MySQLParams): TableParamsQuestions = {
    new TableParamsQuestions(mySQLParams)
  }
}
