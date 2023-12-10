package io.perfix.stores.mysql

import io.perfix.question.mysql.{ConnectionParamsQuestion, TableParamsQuestions}
import io.perfix.question.{Question, Questionnaire}

case class MySQLQuestionnaire(mySQLParams: MySQLParams) extends Questionnaire {
  
  val questions: Iterator[Question] = Seq(
    ConnectionParamsQuestion(mySQLParams),
    TableParamsQuestions(mySQLParams)
  ).iterator

}
