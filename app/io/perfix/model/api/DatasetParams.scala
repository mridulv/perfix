package io.perfix.model.api

import io.perfix.generator.FakeDataGenerator
import io.perfix.model.{ColumnDescription, UserInfo}
import io.perfix.db.tables.DatasetConfigRow
import play.api.libs.json.{Format, Json}

import scala.util.Random

case class DatasetParams(id: Option[DatasetId],
                         name: String,
                         description: String,
                         rows: Int,
                         columns: Seq[ColumnDescription],
                         createdAt: Option[Long] = None) {

  private val faker = new FakeDataGenerator
  lazy val dataset: Dataset = faker.generateData(this)

  def toDatasetConfigParams(userInfo: UserInfo): DatasetConfigRow = {
    id match {
      case Some(id) =>
        DatasetConfigRow(id = id.id, userEmail = userInfo.email, obj = Json.toJson(this).toString())
      case None =>
        DatasetConfigRow(id = -1, userEmail = userInfo.email, obj = Json.toJson(this).toString())
    }
  }

}

object DatasetParams {
  implicit val DatasetParamsFormatter: Format[DatasetParams] = Json.format[DatasetParams]

  def empty(): DatasetParams = {
    DatasetParams(
      None,
      name = s"dataset-${Random.nextInt()}",
      description = "Empty Dataset",
      0,
      columns = Seq.empty,
      createdAt = Some(System.currentTimeMillis())
    )
  }
}
