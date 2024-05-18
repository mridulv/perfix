package io.perfix.model.store

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, Reads, Writes}

sealed trait StoreType {
  def name: String
}

object StoreType {
  case object MySQLStoreType extends StoreType {
    val name = "MySQL"
  }

  case object RedisStoreType extends StoreType {
    val name = "Redis"
  }

  case object DynamoDBStoreType extends StoreType {
    val name = "DynamoDB"
  }

  case object MongoDBStoreType extends StoreType {
    val name = "DocumentDB"
  }

  implicit val StoreTypeReads: Reads[StoreType] = Reads {
    case JsString("MySQLStoreType") => JsSuccess(MySQLStoreType)
    case JsString("RedisStoreType") => JsSuccess(RedisStoreType)
    case JsString("DynamoDBStoreType") => JsSuccess(DynamoDBStoreType)
    case JsString("MongoDBStoreType") => JsSuccess(MongoDBStoreType)
    case _ => JsError("Invalid DataType")
  }

  // Define a custom Writes for serialization
  implicit val StoreTypeWrites: Writes[StoreType] = Writes {
    case MySQLStoreType => JsString("MySQLStoreType")
    case RedisStoreType => JsString("RedisStoreType")
    case DynamoDBStoreType => JsString("DynamoDBStoreType")
    case MongoDBStoreType => JsString("MongoDBStoreType")
  }

  implicit val StoreTypeFormat: Format[StoreType] = Format(StoreTypeReads, StoreTypeWrites)

}
