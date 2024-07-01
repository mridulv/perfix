package io.perfix.model.api

import io.perfix.stores.Database.{DocumentDBDatabase, DynamoDBDatabase, MySQLDatabase, RedisDatabase}
import io.perfix.model._
import io.perfix.model.store.StoreType.{DynamoDB, MongoDB, MySQL, Redis, StoreType}
import play.api.libs.json.{Format, Json}

case class DatabaseFormInput(forms: Seq[FormInputs])

object DatabaseFormInput {
  implicit val DatabaseFormInputFormatter: Format[DatabaseFormInput] = Json.format[DatabaseFormInput]
}
