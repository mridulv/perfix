package io.perfix.model

import io.perfix.forms.Form.FormInputName
import play.api.libs.json.{Json, Reads, Writes}

case class FormInputValues(values: Seq[FormInputValue]) {

  def toMap: Map[FormInputName, Any] = {
    values.map { ans =>
      ans.inputName -> ans.answer
    }.toMap
  }

  def addFormInputValue(formInputValue: FormInputValue): FormInputValues = {
    this.copy(values ++ Seq(formInputValue))
  }

  def addFormInputValues(formInputValues: Seq[FormInputValue]): FormInputValues = {
    this.copy(values ++ formInputValues)
  }

}

object FormInputValues {
  implicit val FormInputValuesWrites: Writes[FormInputValues] = Json.writes[FormInputValues]
  implicit val FormInputValuesReads: Reads[FormInputValues] = Json.reads[FormInputValues]

  def empty: FormInputValues = {
    FormInputValues(Seq.empty)
  }
}
