package io.perfix.question

import Question.QuestionLabel
import io.perfix.model.DataType

trait Question {

  val mapping: Map[QuestionLabel, DataType]

  val storeQuestionParams: QuestionParams

  def shouldAsk(): Boolean

  def setAnswers(answers: Map[QuestionLabel, Any]): Unit

}

object Question {
  type QuestionLabel = String
}
