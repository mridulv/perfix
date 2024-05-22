package io.perfix.forms.mysql

import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.forms.Form
import io.perfix.forms.Form.FormInputName
import io.perfix.forms.mysql.MySQLConnectionParamsForm._
import io.perfix.model.{FormInputType, StringType}
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLParams}

class MySQLConnectionParamsForm(override val formParams: MySQLParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    URL -> FormInputType(StringType),
    USERNAME -> FormInputType(StringType),
    PASSWORD -> FormInputType(StringType)
  )

  override def shouldAsk(): Boolean = {
    import formParams._
    mySQLConnectionParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import formParams._
    mySQLConnectionParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLConnectionParams")
      case None =>
        mySQLConnectionParams = Some(MySQLConnectionParams(answers(URL).toString, answers(USERNAME).toString, answers(PASSWORD).toString))
    }
  }
}

object MySQLConnectionParamsForm {
  val URL = "url"
  val USERNAME = "username"
  val PASSWORD = "password"

  def apply(mySQLParams: MySQLParams): MySQLConnectionParamsForm = {
    new MySQLConnectionParamsForm(mySQLParams)
  }
}
