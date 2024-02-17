package io.perfix.launch

import io.perfix.question.QuestionParams

class AWSCloudParams extends QuestionParams {

  var accessKey: Option[String] = None
  var accessSecret: Option[String] = None
  var launchDB: Boolean = false
  var useInstanceRole: Boolean = false

}
