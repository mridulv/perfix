package io.perfix.model.store

import io.perfix.model.store.StoreType.{DynamoDBVal, MongoDBVal, MySQLVal, Redis, RedisVal}
import play.api.libs.json._

trait StoreParams

object StoreParams {
  import DocumentDBStoreParams._
  import DynamoDBStoreParams._
  import MySQLStoreParams._
  import RedisStoreParams._

  implicit val storeParamsReads: Reads[StoreParams] = (json: JsValue) => {
    (json \ "type").validate[String].flatMap {
      case RedisVal     => json.validate[RedisStoreParams]
      case MySQLVal    => json.validate[MySQLStoreParams]
      case DynamoDBVal  => json.validate[DynamoDBStoreParams]
      case MongoDBVal => json.validate[DocumentDBStoreParams]
      case other       => JsError(s"Unknown type: $other")
    }
  }

  implicit val storeParamsWrites: Writes[StoreParams] = {
    case params: RedisStoreParams    => Json.toJson(params).as[JsObject] + ("type" -> JsString(RedisVal))
    case params: MySQLStoreParams    => Json.toJson(params).as[JsObject] + ("type" -> JsString(MySQLVal))
    case params: DynamoDBStoreParams => Json.toJson(params).as[JsObject] + ("type" -> JsString(DynamoDBVal))
    case params: DocumentDBStoreParams => Json.toJson(params).as[JsObject] + ("type" -> JsString(MongoDBVal))
  }
}