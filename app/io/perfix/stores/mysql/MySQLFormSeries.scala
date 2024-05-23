package io.perfix.stores.mysql

import io.perfix.forms.mysql.{MySQLConnectionParamsForm, MySQLTableIndicesDetailForm, MySQLTableParamsForm}
import io.perfix.forms.{Form, FormSeries}

case class MySQLFormSeries(mySQLParams: MySQLParams) extends FormSeries {
  
  val forms: Iterator[Form] = Seq(
    MySQLConnectionParamsForm(mySQLParams),
    MySQLTableParamsForm(mySQLParams),
    MySQLTableIndicesDetailForm(mySQLParams)
  ).iterator

}
