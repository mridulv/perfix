package io.perfix.question.mysql

import ConnectionParamsQuestion._
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{DataType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLParams}

class ConnectionParamsQuestion(override val storeQuestionParams: MySQLParams) extends Question {

  override val mapping: Map[QuestionLabel, DataType] = Map(
    URL -> StringType,
    USERNAME -> StringType,
    PASSWORD -> StringType
  )

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    mySQLConnectionParams.isEmpty
  }

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    import storeQuestionParams._
    mySQLConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        mySQLConnectionParams = Some(MySQLConnectionParams(answers(URL).toString, answers(USERNAME).toString, answers(PASSWORD).toString))
    }
  }
}

object ConnectionParamsQuestion {
  val URL = "url"
  val USERNAME = "username"
  val PASSWORD = "password"

  def apply(mySQLParams: MySQLParams): ConnectionParamsQuestion = {
    new ConnectionParamsQuestion(mySQLParams)
  }
}
