package io.perfix.stores.mysql

import io.perfix.question.{Question, Questionnaire}
import io.perfix.question.mysql.{ConnectionParamsQuestion, TableIndicesDetailQuestion, TableParamsQuestions}

case class MySQLQuestionnaire(mySQLParams: MySQLParams) extends Questionnaire {
  
  val questions: Iterator[Question] = Seq(
    ConnectionParamsQuestion(mySQLParams),
    TableParamsQuestions(mySQLParams),
    TableIndicesDetailQuestion(mySQLParams)
  ).iterator

}
