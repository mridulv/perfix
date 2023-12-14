package io.perfix.question.mysql

import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.StringType
import io.perfix.question.Question
import io.perfix.question.mysql.ConnectionParamsQuestion._
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLParams}

class ConnectionParamsQuestion(override val storeQuestionParams: MySQLParams,
                               override val questionExecutionContext: QuestionExecutionContext) extends Question {

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    mySQLConnectionParams.isEmpty
  }

  override def evaluateQuestion(): Unit = {
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
  private val URL = "url"
  private val USERNAME = "username"
  private val PASSWORD = "password"

  def apply(mySQLParams: MySQLParams,
            questionExecutionContext: QuestionExecutionContext): ConnectionParamsQuestion = {
    new ConnectionParamsQuestion(mySQLParams, questionExecutionContext)
  }
}
