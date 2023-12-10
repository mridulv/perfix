package io.perfix.query

case class PerfixQueryFilter(field: String, fieldValue: Any) {

  override def toString: String = {
    fieldValue match {
      case Int | Boolean | Double | Float => s"$field = $fieldValue"
      case String => s"$field = \"$fieldValue\""
    }
  }

}
