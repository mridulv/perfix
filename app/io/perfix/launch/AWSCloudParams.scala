package io.perfix.launch

import io.perfix.question.FormParams

class AWSCloudParams extends FormParams {

  var accessKey: Option[String] = None
  var accessSecret: Option[String] = None
  var launchDB: Boolean = false
  var useInstanceRole: Boolean = false

}
