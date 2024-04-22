package io.perfix.model

import io.perfix.generator.FakeDataGenerator
import play.api.libs.json.{Format, Json}

import scala.util.Random

case class DatasetParams(id: Option[DatasetId], name: String, rows: Int, columns: Seq[ColumnDescription]) {

  private val faker = new FakeDataGenerator
  lazy val dataset: Dataset = faker.generateData(this)

}

object DatasetParams {
  implicit val DatasetParamsFormatter: Format[DatasetParams] = Json.format[DatasetParams]

  def empty(): DatasetParams = {
    DatasetParams(None, name = s"dataset-${Random.nextInt()}", 0, columns = Seq.empty)
  }
}
