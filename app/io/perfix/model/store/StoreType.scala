package io.perfix.model.store

import play.api.libs.json._

object StoreType extends Enumeration {
  type StoreType = Value
  val MySQL, Redis, DynamoDB, MongoDB = Value

  implicit val StoreTypeReads: Reads[StoreType.Value] = Reads {
    case JsString("MySQL") => JsSuccess(MySQL)
    case JsString("Redis") => JsSuccess(Redis)
    case JsString("DynamoDB") => JsSuccess(DynamoDB)
    case JsString("MongoDB") => JsSuccess(MongoDB)
    case _ => JsError("Invalid DataType")
  }

  // Define a custom Writes for serialization
  implicit val StoreTypeWrites: Writes[StoreType.Value] = Writes {
    case MySQL => JsString("MySQL")
    case Redis => JsString("Redis")
    case DynamoDB => JsString("DynamoDB")
    case MongoDB => JsString("MongoDB")
  }

  implicit val StoreTypeFormat: Format[StoreType] = Format(StoreTypeReads, StoreTypeWrites)

}
