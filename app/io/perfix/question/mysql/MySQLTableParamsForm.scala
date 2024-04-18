package io.perfix.question.mysql

import MySQLTableParamsForm._
import io.perfix.exceptions.ParamsAlreadyDefinedException
import io.perfix.model.{FormInputType, StringType}
import io.perfix.question.Form
import io.perfix.question.Form.FormInputName
import io.perfix.stores.mysql.{MySQLParams, MySQLTableParams}

class MySQLTableParamsForm(override val formParams: MySQLParams) extends Form {

  override val mapping: Map[FormInputName, FormInputType] = Map(
    DBNAME -> FormInputType(StringType),
    TABLENAME -> FormInputType(StringType)
  )

  override def shouldAsk(): Boolean = {
    formParams.mySQLTableParams.isEmpty
  }

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    import formParams._
    mySQLTableParams match {
      case Some(_) => throw ParamsAlreadyDefinedException("mySQLTableParams")
      case None => mySQLTableParams = Some(MySQLTableParams(answers(DBNAME).toString, answers(TABLENAME).toString))
    }
  }

}

object MySQLTableParamsForm {
  val DBNAME = "dbName"
  val TABLENAME = "tableName"

  def apply(mySQLParams: MySQLParams): MySQLTableParamsForm = {
    new MySQLTableParamsForm(mySQLParams)
  }
}
