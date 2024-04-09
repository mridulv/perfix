package io.perfix.model

import io.perfix.generator.FakeDataGenerator
import play.api.libs.json.{Format, Json}

case class DatasetParams(rows: Int, columns: Seq[ColumnDescription]) {

  private val faker = new FakeDataGenerator
  lazy val dataset: Dataset = faker.generateData(this)

}

object DatasetParams {
  implicit val DatasetParamsFormatter: Format[DatasetParams] = Json.format[DatasetParams]
}
