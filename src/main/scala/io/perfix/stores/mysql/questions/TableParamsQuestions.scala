package io.perfix.stores.mysql.questions

import io.perfix.StringType
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.stores.mysql.{MySQLParams, MySQLTableParams}
import io.perfix.stores.question.Question

class TableParamsQuestions(override val storeQuestionParams: MySQLParams) extends Question {

  private val DBNAME = "dbName"
  private val TABLENAME = "tableName"

  override def shouldAsk(): Boolean = {
    storeQuestionParams.mySQLTableParams.isEmpty
  }

  override def evaluateQuestions(): Unit = {
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
