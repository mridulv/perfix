package io.perfix.model

import io.perfix.forms.Form.FormInputName
import play.api.libs.json._

case class FormInputs(inputs: Map[FormInputName, FormInputType])

object FormInputs {
  implicit val reads: Reads[FormInputs] = Json.reads[FormInputs]
  implicit val writes: Writes[FormInputs] = Json.writes[FormInputs]
}
