package io.perfix.model

import play.api.libs.json.{Format, Json}

case class UserInfo(name: Option[String], email: Option[String])

object UserInfo {
  implicit val userInfoFormat: Format[UserInfo] = Json.format[UserInfo]
}
