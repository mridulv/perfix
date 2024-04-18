package io.perfix.stores.mysql

import io.perfix.question.{Form, FormSeries}
import io.perfix.question.mysql.{MySQLConnectionParamsForm, MySQLTableIndicesDetailForm, MySQLTableParamsForm}

case class MySQLFormSeries(mySQLParams: MySQLParams) extends FormSeries {
  
  val forms: Iterator[Form] = Seq(
    MySQLConnectionParamsForm(mySQLParams),
    MySQLTableParamsForm(mySQLParams),
    MySQLTableIndicesDetailForm(mySQLParams)
  ).iterator

}
