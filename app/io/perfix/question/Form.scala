package io.perfix.question

import Form.FormInputName
import io.perfix.model.FormInputType

trait Form {

  val mapping: Map[FormInputName, FormInputType]

  val storeQuestionParams: FormParams

  def shouldAsk(): Boolean

  def setAnswers(answers: Map[FormInputName, Any]): Unit

}

object Form {
  type FormInputName = String

  def filteredAnswers(answers: Map[FormInputName, Option[Any]]): Map[FormInputName, Any] = {
    answers.collect {
      case (label, Some(value)) => (label, value)
    }
  }
}
