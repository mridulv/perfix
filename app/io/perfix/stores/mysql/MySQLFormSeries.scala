package io.perfix.stores.mysql

import io.perfix.question.{Form, FormSeries}
import io.perfix.question.mysql.{ConnectionParamsForm, TableIndicesDetailForm, TableParamsQuestions}

case class MySQLFormSeries(mySQLParams: MySQLParams) extends FormSeries {
  
  val forms: Iterator[Form] = Seq(
    ConnectionParamsForm(mySQLParams),
    TableParamsQuestions(mySQLParams),
    TableIndicesDetailForm(mySQLParams)
  ).iterator

}
