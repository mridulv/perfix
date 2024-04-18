package io.perfix.query

import play.api.libs.json.{Format, Json}
import io.perfix.model.FormInputValue._

case class PerfixQueryFilter(field: String, fieldValue: Any) {

  override def toString: String = {
    fieldValue match {
      case _: String => s"$field = \"$fieldValue\""
      case _ => s"$field = $fieldValue"
    }
  }

}

object PerfixQueryFilter {
  implicit val PerfixQueryFilterFormatter: Format[PerfixQueryFilter] = Json.format[PerfixQueryFilter]
}
