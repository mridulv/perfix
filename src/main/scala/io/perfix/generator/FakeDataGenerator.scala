package io.perfix.generator

import com.github.javafaker.Faker
import io.perfix.model.{ColumnType, DataDescription}

class FakeDataGenerator extends DataGenerator {
  val faker = new Faker

  override def generateData(dataDescription: DataDescription): Seq[Map[String, Any]] = {
    (1 to dataDescription.rows).map { _ =>
      dataDescription.columns.map { columnDesc =>
        columnDesc.columnName -> columnDesc.columnType.getValue
      }.toMap
    }
  }
}
