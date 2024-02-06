package io.perfix.question

import io.perfix.launch.AWSCloudCredentials
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.AWSCredentialsQuestion._
import io.perfix.question.Question.QuestionLabel

class AWSCredentialsQuestion(override val storeQuestionParams: AWSCloudCredentials) extends Question {
  override val mapping: Map[QuestionLabel, QuestionType] = Map(
    AWS_ACCESS_KEY -> QuestionType(StringType),
    AWS_ACCESS_SECRET -> QuestionType(StringType)
  )

  override def shouldAsk(): Boolean = true

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    storeQuestionParams.accessKey = Some(answers(AWS_ACCESS_KEY).toString)
    storeQuestionParams.accessSecret = Some(answers(AWS_ACCESS_SECRET).toString)
  }
}

object AWSCredentialsQuestion {
  val AWS_ACCESS_KEY = "access_key"
  val AWS_ACCESS_SECRET = "access_secret"
}