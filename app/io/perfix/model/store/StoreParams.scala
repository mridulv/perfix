package io.perfix.model.store

import play.api.libs.json._

trait StoreParams

object StoreParams {
  import RedisStoreParams._
  import DocumentDBStoreParams._
  import MySQLStoreParams._
  import DynamoDBStoreParams._

  implicit val storeParamsReads: Reads[StoreParams] = (json: JsValue) => {
    (json \ "type").validate[String].flatMap {
      case "Redis"     => json.validate[RedisStoreParams]
      case "MySQL"     => json.validate[MySQLStoreParams]
      case "DynamoDB"  => json.validate[DynamoDBStoreParams]
      case "DocumentDB" => json.validate[DocumentDBStoreParams]
      case other       => JsError(s"Unknown type: $other")
    }
  }

  implicit val storeParamsWrites: Writes[StoreParams] = {
    case params: RedisStoreParams    => Json.toJson(params).as[JsObject] + ("type" -> JsString("Redis"))
    case params: MySQLStoreParams    => Json.toJson(params).as[JsObject] + ("type" -> JsString("MySQL"))
    case params: DynamoDBStoreParams => Json.toJson(params).as[JsObject] + ("type" -> JsString("DynamoDB"))
    case params: DocumentDBStoreParams => Json.toJson(params).as[JsObject] + ("type" -> JsString("DocumentDB"))
  }
}