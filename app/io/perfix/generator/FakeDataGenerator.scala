package io.perfix.generator

import com.github.javafaker.Faker
import io.perfix.model.api.{Dataset, DatasetParams}

import scala.util.Random

class FakeDataGenerator extends DataGenerator {
  val faker = new Faker

  override def generateData(datasetParams: DatasetParams): Dataset = {
    val numRows = datasetParams.rows
    val columns = datasetParams.columns

    var rows = List[Map[String, Any]]()
    val uniqueValuesByColumn = scala.collection.mutable.Map[String, Set[Any]]()
    for (_ <- 1 to numRows) {
      var row = Map[String, Any]()

      for (column <- columns) {
        val columnName = column.columnName
        val columnType = column.columnType
        var value = columnType.getValue
        if (columnType.isUnique) {
          while (uniqueValuesByColumn.getOrElse(columnName, Set.empty).contains(value)) {
            value = columnType.getValue
          }
          uniqueValuesByColumn.update(columnName, uniqueValuesByColumn.getOrElse(columnName, Set.empty) + value)
        }
        column.valueProbabilities match {
          case Some(valueProbabilities) =>
            value = valueProbabilities.generateValue(value, () => Random.nextInt(100))
          case _ => ()
        }
        row += (columnName -> value)
      }
      rows = row :: rows
    }
    rows = rows.reverse
    Dataset(datasetParams, rows)
  }
}
