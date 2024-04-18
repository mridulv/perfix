package io.perfix.forms

import io.perfix.launch.AWSCloudParams
import io.perfix.model.{BooleanType, FormInputType, StringType}
import io.perfix.forms.AWSCloudParamsForm._
import io.perfix.forms.Form.FormInputName

class AWSCloudParamsForm(override val formParams: AWSCloudParams) extends Form {
  override val mapping: Map[FormInputName, FormInputType] = Map(
    AWS_ACCESS_KEY -> FormInputType(StringType),
    AWS_ACCESS_SECRET -> FormInputType(StringType),
    LAUNCH_DB -> FormInputType(StringType, isRequired = false),
    USE_INSTANCE_ROLE -> FormInputType(BooleanType, isRequired = false)
  )

  override def shouldAsk(): Boolean = true

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    formParams.accessKey = Some(answers(AWS_ACCESS_KEY).toString)
    formParams.accessSecret = Some(answers(AWS_ACCESS_SECRET).toString)
    formParams.launchDB = answers.get(LAUNCH_DB).exists(_.toString.toBoolean)
    formParams.useInstanceRole = answers.get(USE_INSTANCE_ROLE).exists(_.toString.toBoolean)
  }
}

object AWSCloudParamsForm {
  val AWS_ACCESS_KEY = "access_key"
  val AWS_ACCESS_SECRET = "access_secret"
  val LAUNCH_DB = "launch_db"
  val USE_INSTANCE_ROLE = "use_instance_role"
}