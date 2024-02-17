package io.perfix.launch

import io.perfix.question.Question

trait LaunchStoreQuestion extends Question {

  val credentials: AWSCloudParams

  override val shouldAsk: Boolean = credentials.launchDB

}
