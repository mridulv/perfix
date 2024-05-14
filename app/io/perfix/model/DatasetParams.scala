package io.perfix.model

import io.perfix.generator.FakeDataGenerator
import io.perfix.store.tables.DatasetConfigRow
import play.api.libs.json.{Format, Json}

import scala.util.Random

case class DatasetParams(id: Option[DatasetId],
                         name: String,
                         rows: Int,
                         columns: Seq[ColumnDescription],
                         createdAt: Option[Long]) {

  private val faker = new FakeDataGenerator
  lazy val dataset: Dataset = faker.generateData(this)

  def toDatasetConfigParams: DatasetConfigRow = {
    id match {
      case Some(id) =>
        DatasetConfigRow(id = id.id, obj = Json.toJson(this).toString())
      case None =>
        DatasetConfigRow(id = -1, obj = Json.toJson(this).toString())
    }
  }

}

object DatasetParams {
  implicit val DatasetParamsFormatter: Format[DatasetParams] = Json.format[DatasetParams]

  def empty(): DatasetParams = {
    DatasetParams(None, name = s"dataset-${Random.nextInt()}", 0, columns = Seq.empty, createdAt = Some(System.currentTimeMillis()))
  }
}
