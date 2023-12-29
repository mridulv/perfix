package io.perfix.question.mysql

import ConnectionParamsQuestion._
import io.perfix.context.QuestionExecutionContext
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{DataType, StringType}
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.dynamodb.DynamoDBConnectionParametersQuestions.{ACCESS_ID, ACCESS_SECRET, CONNECTION_URL}
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLParams}

class ConnectionParamsQuestion(override val storeQuestionParams: MySQLParams,
                               override val questionExecutionContext: QuestionExecutionContext) extends Question {

  override val mapping: Map[QuestionLabel, DataType] = Map(
    URL -> StringType,
    USERNAME -> StringType,
    PASSWORD -> StringType
  )

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    mySQLConnectionParams.isEmpty
  }

  override def evaluateQuestion(): Unit = {
    import storeQuestionParams._
    mySQLConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        val answers = askQuestions
        mySQLConnectionParams = Some(MySQLConnectionParams(answers(URL).toString, answers(USERNAME).toString, answers(PASSWORD).toString))
    }
  }
}

object ConnectionParamsQuestion {
  val URL = "url"
  val USERNAME = "username"
  val PASSWORD = "password"

  def apply(mySQLParams: MySQLParams,
            questionExecutionContext: QuestionExecutionContext): ConnectionParamsQuestion = {
    new ConnectionParamsQuestion(mySQLParams, questionExecutionContext)
  }
}
