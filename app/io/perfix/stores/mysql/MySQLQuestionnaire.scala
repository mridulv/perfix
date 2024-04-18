package io.perfix.stores.mysql

import io.perfix.question.{Form, Questionnaire}
import io.perfix.question.mysql.{ConnectionParamsForm, TableIndicesDetailForm, TableParamsQuestions}

case class MySQLQuestionnaire(mySQLParams: MySQLParams) extends Questionnaire {
  
  val questions: Iterator[Form] = Seq(
    ConnectionParamsForm(mySQLParams),
    TableParamsQuestions(mySQLParams),
    TableIndicesDetailForm(mySQLParams)
  ).iterator

}
