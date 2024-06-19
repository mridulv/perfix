package io.perfix.model.store

import io.perfix.model.store.StoreType._
import io.perfix.stores.documentdb.DocumentDBDatabaseConfigParams
import io.perfix.stores.mysql.MySQLDatabaseConfigParams
import io.perfix.stores.dynamodb.DynamoDBDatabaseConfigParams
import io.perfix.stores.redis.RedisDatabaseConfigParams
import play.api.libs.json._

trait DatabaseConfigParams

object DatabaseConfigParams {
  import io.perfix.stores.documentdb.DocumentDBDatabaseConfigParams._
  import io.perfix.stores.dynamodb.DynamoDBDatabaseConfigParams._
  import io.perfix.stores.mysql.MySQLDatabaseConfigParams._
  import RedisDatabaseConfigParams._

  implicit val storeParamsReads: Reads[DatabaseConfigParams] = (json: JsValue) => {
    (json \ "type").validate[String].flatMap {
      case Redis.toString     => json.validate[RedisDatabaseConfigParams]
      case MySQL.toString     => json.validate[MySQLDatabaseConfigParams]
      case DynamoDB.toString  => json.validate[DynamoDBDatabaseConfigParams]
      case MongoDB.toString   => json.validate[DocumentDBDatabaseConfigParams]
      case other       => JsError(s"Unknown type: $other")
    }
  }

  implicit val storeParamsWrites: Writes[DatabaseConfigParams] = {
    case params: RedisDatabaseConfigParams    => Json.toJson(params).as[JsObject] + ("type" -> JsString(Redis.toString))
    case params: MySQLDatabaseConfigParams    => Json.toJson(params).as[JsObject] + ("type" -> JsString(MySQL.toString))
    case params: DynamoDBDatabaseConfigParams => Json.toJson(params).as[JsObject] + ("type" -> JsString(DynamoDB.toString))
    case params: DocumentDBDatabaseConfigParams => Json.toJson(params).as[JsObject] + ("type" -> JsString(MongoDB.toString))
  }
}