package io.perfix.model

import play.api.libs.json.{Format, Json}

case class FormInput(inputName: String,
                     inputDisplayName: String,
                     formInputType: FormInputType)

object FormInput {
  implicit val formatter: Format[FormInput] = Json.format[FormInput]
}
