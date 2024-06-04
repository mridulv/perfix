package io.perfix.query

import io.perfix.model.FormInputValue._
import play.api.libs.json.{Format, Json}

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
