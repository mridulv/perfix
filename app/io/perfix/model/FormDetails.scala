package io.perfix.model

import play.api.libs.json.{Format, Json}

case class FormDetails(formStatus: FormStatus, values: FormInputValues) {

  def addValues(formInputValues: FormInputValues): FormDetails = {
    FormDetails(this.formStatus, this.values.addFormInputValues(formInputValues.values))
  }

  def complete: FormDetails = {
    FormDetails(Completed, this.values)
  }

}

object FormDetails {
  implicit val FormDetailsFormatter: Format[FormDetails] = Json.format[FormDetails]

  def empty: FormDetails = {
    FormDetails(InComplete, FormInputValues(Seq.empty))
  }

}
