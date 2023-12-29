package io.perfix.query

case class PerfixQueryFilter(field: String, fieldValue: Any) {

  override def toString: String = {
    fieldValue match {
      case _: String => s"$field = \"$fieldValue\""
      case _ => s"$field = $fieldValue"
    }
  }

}
