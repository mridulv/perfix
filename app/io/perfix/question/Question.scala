package io.perfix.question

import Question.QuestionLabel
import io.perfix.model.QuestionType

trait Question {

  val mapping: Map[QuestionLabel, QuestionType]

  val storeQuestionParams: QuestionParams

  def shouldAsk(): Boolean

  def setAnswers(answers: Map[QuestionLabel, Any]): Unit

}

object Question {
  type QuestionLabel = String

  def filteredAnswers(answers: Map[QuestionLabel, Option[Any]]): Map[QuestionLabel, Any] = {
    answers.collect {
      case (label, Some(value)) => (label, value)
    }
  }
}
