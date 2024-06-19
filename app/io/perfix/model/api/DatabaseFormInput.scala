package io.perfix.model.api

import io.perfix.model.Database.{DocumentDBDatabase, DynamoDBDatabase, MySQLDatabase, RedisDatabase}
import io.perfix.model.{api, _}
import io.perfix.model.store.StoreType.{DynamoDB, MongoDB, MySQL, Redis, StoreType}
import play.api.libs.json.{Format, Json}

case class DatabaseFormInput(forms: Seq[FormInputs])

object DatabaseFormInput {
  implicit val DatabaseFormInputFormatter: Format[DatabaseFormInput] = Json.format[DatabaseFormInput]
}
