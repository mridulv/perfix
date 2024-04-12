package io.perfix.model

import play.api.libs.json.{Format, Json}

case class DatabaseConfigId(id: Int)

object DatabaseConfigId {
  implicit val DatabaseConfigIdFormatter: Format[DatabaseConfigId] = Json.format[DatabaseConfigId]
}
