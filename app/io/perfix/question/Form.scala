package io.perfix.question

import Form.QuestionLabel
import io.perfix.model.QuestionType

trait Form {

  val mapping: Map[QuestionLabel, QuestionType]

  val storeQuestionParams: FormParams

  def shouldAsk(): Boolean

  def setAnswers(answers: Map[QuestionLabel, Any]): Unit

}

object Form {
  type QuestionLabel = String

  def filteredAnswers(answers: Map[QuestionLabel, Option[Any]]): Map[QuestionLabel, Any] = {
    answers.collect {
      case (label, Some(value)) => (label, value)
    }
  }
}
