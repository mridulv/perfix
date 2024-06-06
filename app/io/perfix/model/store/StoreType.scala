package io.perfix.model.store

import play.api.libs.json._

object StoreType extends Enumeration {
  type StoreType = Value
  val MySQLStoreType, RedisStoreType, DynamoDBStoreType, MongoDBStoreType = Value

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
