package io.perfix.model.store

import io.perfix.model.store.StoreType.{DynamoDBVal, MongoDBVal, MySQLVal, Redis, RedisVal}
import io.perfix.stores.documentdb.DocumentDBStoreParams
import io.perfix.stores.mysql.MySQLStoreParams
import io.perfix.stores.dynamodb.DynamoDBStoreParams
import io.perfix.stores.redis.RedisStoreParams
import play.api.libs.json._

trait StoreParams

object StoreParams {
  import io.perfix.stores.documentdb.DocumentDBStoreParams._
  import io.perfix.stores.dynamodb.DynamoDBStoreParams._
  import io.perfix.stores.mysql.MySQLStoreParams._
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