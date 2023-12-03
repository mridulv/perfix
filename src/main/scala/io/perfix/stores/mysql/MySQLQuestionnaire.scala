package io.perfix.stores.mysql

import io.perfix.stores.mysql.questions.{ConnectionParamsQuestion, TableParamsQuestions}
import io.perfix.stores.question.{Question, Questionnaire}

case class MySQLQuestionnaire(mySQLParams: MySQLParams) extends Questionnaire {
  
  val questions: Iterator[Question] = Seq(
    ConnectionParamsQuestion(mySQLParams),
    TableParamsQuestions(mySQLParams)
  ).iterator

}
