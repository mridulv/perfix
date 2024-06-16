package io.perfix.model

import play.api.libs.json._

case class FormInputs(inputs: Seq[FormInput])

object FormInputs {
  implicit val formatter: Format[FormInputs] = Json.format[FormInputs]
}