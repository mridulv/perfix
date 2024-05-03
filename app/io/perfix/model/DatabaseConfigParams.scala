package io.perfix.model

import io.perfix.common.ExperimentExecutor
import io.perfix.launch.AWSCloudParams
import io.perfix.forms.{AWSCloudParamsForm, FormSeries}
import io.perfix.store.tables.{DatabaseConfigRow, ExperimentRow}
import play.api.libs.json.{Format, Json}

case class DatabaseConfigParams(databaseConfigId: Option[DatabaseConfigId] = None,
                                name: String,
                                storeName: String,
                                formDetails: Option[FormDetails] = None) {

  def inputValues(): Option[Seq[FormInputValue]] = {
    formDetails.map(_.values.values)
  }

  def toDatabaseConfigRow: DatabaseConfigRow = {
    databaseConfigId match {
      case Some(id) =>
        DatabaseConfigRow(id = id.id, obj = Json.toJson(this).toString())
      case None =>
        DatabaseConfigRow(id = -1, obj = Json.toJson(this).toString())
    }
  }

}

object DatabaseConfigParams {
  implicit val DatabaseConfigParamsFormatter: Format[DatabaseConfigParams] = Json.format[DatabaseConfigParams]
}