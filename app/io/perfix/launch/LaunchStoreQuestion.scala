package io.perfix.launch

import io.perfix.common.CommonConfig.IS_TRIAL_MODE
import io.perfix.model.QuestionType
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel

trait LaunchStoreQuestion extends Question {

  val credentials: AWSCloudParams
  val launchQuestionsMapping: Map[QuestionLabel, QuestionType]

  override val mapping: Map[QuestionLabel, QuestionType] = if (IS_TRIAL_MODE) {
    Map.empty
  } else {
    launchQuestionsMapping
  }

  override val shouldAsk: Boolean = credentials.launchDB

}
