package io.perfix.model

import io.perfix.forms.Form.FormInputName
import play.api.libs.json.{Json, Reads, Writes}

case class FormInputValues(values: Seq[FormInputValue]) {

  def toMap: Map[FormInputName, Any] = {
    values.map { ans =>
      ans.inputName -> ans.answer
    }.toMap
  }

  def addFormInputValues(formInputValues: Seq[FormInputValue]): FormInputValues = {
    val original = this.toMap
    val toBeAdded = FormInputValues(formInputValues).toMap
    val addedFormInputValues = (original ++ toBeAdded).map { case (k, v) =>
      FormInputValue(k, v)
    }
    FormInputValues(addedFormInputValues.toSeq)
  }

}

object FormInputValues {
  implicit val FormInputValuesWrites: Writes[FormInputValues] = Json.writes[FormInputValues]
  implicit val FormInputValuesReads: Reads[FormInputValues] = Json.reads[FormInputValues]

  def empty: FormInputValues = {
    FormInputValues(Seq.empty)
  }
}
