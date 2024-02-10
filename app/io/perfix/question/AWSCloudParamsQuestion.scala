package io.perfix.question

import io.perfix.launch.AWSCloudParams
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.AWSCloudParamsQuestion._
import io.perfix.question.Question.QuestionLabel

class AWSCloudParamsQuestion(override val storeQuestionParams: AWSCloudParams) extends Question {
  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    AWS_ACCESS_KEY -> QuestionType(StringType),
    AWS_ACCESS_SECRET -> QuestionType(StringType),
    LAUNCH_DB -> QuestionType(StringType, isRequired = false)
  )

  override def shouldAsk(): Boolean = true

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    storeQuestionParams.accessKey = Some(answers(AWS_ACCESS_KEY).toString)
    storeQuestionParams.accessSecret = Some(answers(AWS_ACCESS_SECRET).toString)
    storeQuestionParams.launchDB = answers.get(LAUNCH_DB).exists(_.toString.toBoolean)
  }
}

object AWSCloudParamsQuestion {
  val AWS_ACCESS_KEY = "access_key"
  val AWS_ACCESS_SECRET = "access_secret"
  val LAUNCH_DB = "launch_db"
}