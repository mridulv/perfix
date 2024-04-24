package io.perfix.model

import io.perfix.common.ExperimentExecutor
import io.perfix.launch.AWSCloudParams
import io.perfix.forms.{AWSCloudParamsForm, FormSeries}
import play.api.libs.json.{Format, Json}

case class DatabaseConfigParams(databaseConfigId: Option[DatabaseConfigId] = None,
                                name: String,
                                storeName: String,
                                formInputValues: Option[Seq[FormInputValue]] = None)

object DatabaseConfigParams {
  implicit val DatabaseConfigParamsFormatter: Format[DatabaseConfigParams] = Json.format[DatabaseConfigParams]
}