package io.perfix.model.store

import play.api.libs.json._

object StoreType extends Enumeration {
  type StoreType = Value

  val MySQLVal = "MySQL"
  val RedisVal = "Redis"
  val DynamoDBVal = "DynamoDB"
  val MongoDBVal = "MongoDB"

  val MySQL, Redis, DynamoDB, MongoDB = Value

  implicit val StoreTypeReads: Reads[StoreType.Value] = Reads {
    case JsString(MySQLVal) => JsSuccess(MySQL)
    case JsString(RedisVal) => JsSuccess(Redis)
    case JsString(DynamoDBVal) => JsSuccess(DynamoDB)
    case JsString(MongoDBVal) => JsSuccess(MongoDB)
    case _ => JsError("Invalid DataType")
  }

  // Define a custom Writes for serialization
  implicit val StoreTypeWrites: Writes[StoreType.Value] = Writes {
    case MySQL => JsString(MongoDBVal)
    case Redis => JsString(RedisVal)
    case DynamoDB => JsString(DynamoDBVal)
    case MongoDB => JsString(MongoDBVal)
  }

  implicit val StoreTypeFormat: Format[StoreType] = Format(StoreTypeReads, StoreTypeWrites)

}
