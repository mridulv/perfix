package io.perfix.question

import io.perfix.launch.AWSCloudParams
import io.perfix.model.{BooleanType, FormInputType, StringType}
import io.perfix.question.AWSCloudParamsForm._
import io.perfix.question.Form.FormInputName

class AWSCloudParamsForm(override val storeQuestionParams: AWSCloudParams) extends Form {
  override val mapping: Map[FormInputName, FormInputType] = Map(
    AWS_ACCESS_KEY -> FormInputType(StringType),
    AWS_ACCESS_SECRET -> FormInputType(StringType),
    LAUNCH_DB -> FormInputType(StringType, isRequired = false),
    USE_INSTANCE_ROLE -> FormInputType(BooleanType, isRequired = false)
  )

  override def shouldAsk(): Boolean = true

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    storeQuestionParams.accessKey = Some(answers(AWS_ACCESS_KEY).toString)
    storeQuestionParams.accessSecret = Some(answers(AWS_ACCESS_SECRET).toString)
    storeQuestionParams.launchDB = answers.get(LAUNCH_DB).exists(_.toString.toBoolean)
    storeQuestionParams.useInstanceRole = answers.get(USE_INSTANCE_ROLE).exists(_.toString.toBoolean)
  }
}

object AWSCloudParamsForm {
  val AWS_ACCESS_KEY = "access_key"
  val AWS_ACCESS_SECRET = "access_secret"
  val LAUNCH_DB = "launch_db"
  val USE_INSTANCE_ROLE = "use_instance_role"
}