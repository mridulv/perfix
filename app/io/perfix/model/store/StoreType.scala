package io.perfix.model.store

import play.api.libs.json._

object StoreType extends Enumeration {
  type StoreType = Value

  val MySQL, Redis, DynamoDB, MongoDB = Value

  implicit val writes: Writes[StoreType] = Writes[StoreType] { category =>
    JsString(category.toString)
  }

  implicit val reads: Reads[StoreType] = Reads[StoreType] {
    case JsString(str) => JsSuccess(StoreType.withName(str))
    case _ => JsError("Invalid value for StoreType")
  }


  implicit val StoreTypeFormat: Format[StoreType] = Format(reads, writes)

}
