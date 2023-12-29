package io.perfix.generator

import com.github.javafaker.Faker
import io.perfix.model.{ColumnType, DataDescription, DataWithDescription}

class FakeDataGenerator extends DataGenerator {
  val faker = new Faker

  override def generateData(dataDescription: DataDescription): DataWithDescription = {
    val data = (1 to dataDescription.rows).map { _ =>
      dataDescription.columns.map { columnDesc =>
        columnDesc.columnName -> columnDesc.columnType.getValue
      }.toMap
    }
    DataWithDescription(dataDescription, data)
  }
}
