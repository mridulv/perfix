package io.perfix.model

import io.perfix.question.Form.FormInputName
import play.api.libs.json.{Json, Reads, Writes}

case class FormInputValues(values: Seq[FormInputValue]) {

  def toMap: Map[FormInputName, Any] = {
    values.map { ans =>
      ans.inputName -> ans.answer
    }.toMap
  }

  def addPerfixQuestionAnswer(perfixQuestionAnswer: FormInputValue): FormInputValues = {
    this.copy(values ++ Seq(perfixQuestionAnswer))
  }

  def addPerfixQuestionAnswers(perfixQuestionAnswers: Seq[FormInputValue]): FormInputValues = {
    this.copy(values ++ perfixQuestionAnswers)
  }

}

object FormInputValues {
  implicit val PerfixQuestionAnswersWrites: Writes[FormInputValues] = Json.writes[FormInputValues]
  implicit val PerfixQuestionAnswersReads: Reads[FormInputValues] = Json.reads[FormInputValues]

  def empty: FormInputValues = {
    FormInputValues(Seq.empty)
  }
}
