package io.perfix.query

import play.api.libs.json.{Format, Json}
import io.perfix.model.ValueProbability._

case class DbQueryFilter(field: String, fieldValue: Any) {

  override def toString: String = {
    fieldValue match {
      case _: String => s"$field = \"$fieldValue\""
      case _ => s"$field = $fieldValue"
    }
  }

}

object DbQueryFilter {
  implicit val DbQueryFilterFormatter: Format[DbQueryFilter] = Json.format[DbQueryFilter]
}
