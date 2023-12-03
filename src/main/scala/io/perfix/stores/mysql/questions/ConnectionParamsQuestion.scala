package io.perfix.stores.mysql.questions

import io.perfix.StringType
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLParams}
import io.perfix.stores.question.Question

class ConnectionParamsQuestion(override val storeQuestionParams: MySQLParams) extends Question {

  private val URL = "url"
  private val USERNAME = "username"
  private val PASSWORD = "password"

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    mySQLConnectionParams.isEmpty
  }

  override def evaluateQuestions(): Unit = {
    import storeQuestionParams._
    mySQLConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        val questionMapping = Map(
          URL -> StringType,
          USERNAME -> StringType,
          PASSWORD -> StringType
        )
        val answers = askQuestions(questionMapping)
        MySQLConnectionParams(answers(URL).toString, answers(USERNAME).toString, answers(PASSWORD).toString)
    }
  }
}

object ConnectionParamsQuestion {
  def apply(mySQLParams: MySQLParams): ConnectionParamsQuestion = {
    new ConnectionParamsQuestion(mySQLParams)
  }
}
