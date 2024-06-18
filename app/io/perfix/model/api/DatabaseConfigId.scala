package io.perfix.model.api

import play.api.libs.json.{Format, Json}

case class DatabaseConfigId(id: Int)

object DatabaseConfigId {
  implicit val DatabaseConfigIdFormatter: Format[DatabaseConfigId] = Json.format[DatabaseConfigId]
}
