package io.perfix.model.store

import io.perfix.model.api.DatabaseConfigParams
import io.perfix.model.store.StoreType._
import io.perfix.stores.documentdb.DocumentDBDatabaseSetupParams
import io.perfix.stores.mysql.RDSDatabaseSetupParams
import io.perfix.stores.dynamodb.DynamoDBDatabaseSetupParams
import io.perfix.stores.redis.RedisDatabaseSetupParams
import play.api.libs.json._

trait DatabaseSetupParams

object DatabaseSetupParams {
  import io.perfix.stores.documentdb.DocumentDBDatabaseSetupParams._
  import io.perfix.stores.dynamodb.DynamoDBDatabaseSetupParams._
  import io.perfix.stores.mysql.RDSDatabaseSetupParams._
  import RedisDatabaseSetupParams._

  implicit val storeParamsReads: Reads[DatabaseSetupParams] = (json: JsValue) => {
    (json \ "type").validate[String].flatMap { tp =>
      StoreType.withName(tp) match {
        case Redis     => json.validate[RedisDatabaseSetupParams]
        case MySQL     => json.validate[RDSDatabaseSetupParams]
        case DynamoDB  => json.validate[DynamoDBDatabaseSetupParams]
        case MongoDB   => json.validate[DocumentDBDatabaseSetupParams]
        case other       => JsError(s"Unknown type: $other")
      }
    }
  }

  implicit val storeParamsWrites: Writes[DatabaseSetupParams] = {
    case params: RedisDatabaseSetupParams    => Json.toJson(params).as[JsObject] + ("type" -> JsString(Redis.toString))
    case params: RDSDatabaseSetupParams    => Json.toJson(params).as[JsObject] + ("type" -> JsString(MySQL.toString))
    case params: DynamoDBDatabaseSetupParams => Json.toJson(params).as[JsObject] + ("type" -> JsString(DynamoDB.toString))
    case params: DocumentDBDatabaseSetupParams => Json.toJson(params).as[JsObject] + ("type" -> JsString(MongoDB.toString))
  }

  implicit val DatabaseSetupParamsFormatter: Format[DatabaseSetupParams] = Format(storeParamsReads, storeParamsWrites)
}