package io.perfix.launch

import io.perfix.question.Question

trait LaunchStoreQuestion extends Question {

  val credentials: AWSCloudCredentials

  override val shouldAsk: Boolean = true

}
