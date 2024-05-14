package io.perfix.model

import io.perfix.model.experiment.ExperimentParams
import play.api.libs.json.{Format, JsError, JsObject, JsString, JsValue, Json, Reads, Writes}

trait EntityFilter

trait DatasetFilter extends EntityFilter {
  def filterDataset(dataset: Dataset): Boolean
}

trait DatabaseConfigFilter extends EntityFilter {
  def filterDatabaseConfig(databaseConfigParams: DatabaseConfigParams): Boolean
}

trait ExperimentFilter extends EntityFilter {
  def filterExperiment(experimentParams: ExperimentParams): Boolean
}

case class TextFilter(text: String) extends DatasetFilter with DatabaseConfigFilter with ExperimentFilter {
  override def filterDataset(dataset: Dataset): Boolean = {
    dataset.params.name.contains(text)
  }

  override def filterDatabaseConfig(databaseConfigParams: DatabaseConfigParams): Boolean = {
    databaseConfigParams.name.contains(text)
  }

  override def filterExperiment(experimentParams: ExperimentParams): Boolean = {
    experimentParams.name.contains(text)
  }
}

case class DatabaseTypeFilter(store: String) extends DatabaseConfigFilter {
  override def filterDatabaseConfig(databaseConfigParams: DatabaseConfigParams): Boolean = {
    databaseConfigParams.dataStore.name == store
  }
}

case class DatasetFilter(name: String) extends DatasetFilter {

  override def filterDataset(dataset: Dataset): Boolean = {
    dataset.params.name == name
  }

}

case class ExperimentState(name: String) extends ExperimentFilter {

  override def filterExperiment(experimentParams: ExperimentParams): Boolean = {
    experimentParams.experimentState.name == name
  }

}

object EntityFilter {
  implicit val textFilterFormat: Format[TextFilter] = Json.format[TextFilter]
  implicit val databaseTypeFilterFormat: Format[DatabaseTypeFilter] = Json.format[DatabaseTypeFilter]
  implicit val datasetFilterFormat: Format[DatasetFilter] = Json.format[DatasetFilter]
  implicit val experimentStateFormat: Format[ExperimentState] = Json.format[ExperimentState]

  implicit val filtersReads: Reads[EntityFilter] = (json: JsValue) => {
    (json \ "type").validate[String].flatMap {
      case "TextFilter"         => json.validate[TextFilter]
      case "DatabaseTypeFilter" => json.validate[DatabaseTypeFilter]
      case "DatasetFilter"      => json.validate[DatasetFilter]
      case "ExperimentState"    => json.validate[ExperimentState]
      case other                => JsError(s"Unknown type: $other")
    }
  }

  implicit val filtersWrites: Writes[EntityFilter] = {
    case filter: TextFilter          => Json.toJson(filter)(textFilterFormat).as[JsObject] + ("type" -> JsString("TextFilter"))
    case filter: DatabaseTypeFilter  => Json.toJson(filter)(databaseTypeFilterFormat).as[JsObject] + ("type" -> JsString("DatabaseTypeFilter"))
    case filter: DatasetFilter       => Json.toJson(filter)(datasetFilterFormat).as[JsObject] + ("type" -> JsString("DatasetFilter"))
    case filter: ExperimentState     => Json.toJson(filter)(experimentStateFormat).as[JsObject] + ("type" -> JsString("ExperimentState"))
  }

  implicit val filtersFormat: Format[EntityFilter] = Format(filtersReads, filtersWrites)
}