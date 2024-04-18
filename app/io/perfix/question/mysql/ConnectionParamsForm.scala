package io.perfix.question.mysql

import ConnectionParamsForm._
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Form
import io.perfix.question.Form.FormInputName
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLParams}

class ConnectionParamsForm(override val storeQuestionParams: MySQLParams) extends Form {

  override val mapping: Map[FormInputName, QuestionType] = Map(
    URL -> QuestionType(StringType),
    USERNAME -> QuestionType(StringType),
    PASSWORD -> QuestionType(StringType)
  )

  override def shouldAsk(): Boolean = {
    import storeQuestionParams._
    mySQLConnectionParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import storeQuestionParams._
    mySQLConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        mySQLConnectionParams = Some(MySQLConnectionParams(answers(URL).toString, answers(USERNAME).toString, answers(PASSWORD).toString))
    }
  }
}

object ConnectionParamsForm {
  val URL = "url"
  val USERNAME = "username"
  val PASSWORD = "password"

  def apply(mySQLParams: MySQLParams): ConnectionParamsForm = {
    new ConnectionParamsForm(mySQLParams)
  }
}
