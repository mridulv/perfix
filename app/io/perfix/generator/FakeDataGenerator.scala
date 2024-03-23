package io.perfix.generator

import com.github.javafaker.Faker
import io.perfix.model.{DataDescription, DataWithDescription}

import scala.util.Random

class FakeDataGenerator extends DataGenerator {
  val faker = new Faker

  override def generateData(dataDescription: DataDescription): DataWithDescription = {
    val numRows = dataDescription.rows
    val columns = dataDescription.columns

    var rows = List[Map[String, Any]]()
    val uniqueValuesByColumn = scala.collection.mutable.Map[String, Set[Any]]()
    for (_ <- 1 to numRows) {
      var row = Map[String, Any]()

      for (column <- columns) {
        val columnName = column.columnName
        val columnType = column.columnType
        var value = columnType.getValue
        val randomNum = Random.nextInt(10000) + 1
        if (columnType.isUnique) {
          while (uniqueValuesByColumn.getOrElse(columnName, Set.empty).contains(value)) {
            value = columnType.getValue
          }
          uniqueValuesByColumn.update(columnName, uniqueValuesByColumn.getOrElse(columnName, Set.empty) + value)
        }
        column.columnValueDistribution match {
          case Some(valueProbability) =>
            if (randomNum < valueProbability.probability * 10000) {
              value = valueProbability.value
            }
          case _ => ()
        }
        row += (columnName -> value)
      }
      rows = row :: rows
    }
    rows = rows.reverse
    val dataWithDescription = DataWithDescription(dataDescription, rows)

    dataWithDescription
  }
}
