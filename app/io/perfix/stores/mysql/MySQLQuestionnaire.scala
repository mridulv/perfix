package io.perfix.stores.mysql

import io.perfix.context.QuestionExecutionContext
import io.perfix.question.{Question, Questionnaire}
import io.perfix.question.mysql.{ConnectionParamsQuestion, TableParamsQuestions}

case class MySQLQuestionnaire(mySQLParams: MySQLParams,
                              questionExecutionContext: QuestionExecutionContext) extends Questionnaire {
  
  val questions: Iterator[Question] = Seq(
    ConnectionParamsQuestion(mySQLParams, questionExecutionContext),
    TableParamsQuestions(mySQLParams, questionExecutionContext)
  ).iterator

}
