package io.perfix.launch

import io.perfix.question.QuestionParams

class AWSCloudCredentials extends QuestionParams {

  var accessKey: Option[String] = None
  var accessSecret: Option[String] = None

}
