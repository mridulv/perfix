package io.perfix.model

import io.perfix.model.api.{DatabaseConfigParams, DatasetParams}
import io.perfix.model.experiment.ExperimentParams
import play.api.libs.json._

trait EntityFilter {

  val filterName: String

  def filter(entityName: String): Boolean = {
    filterName.toLowerCase == entityName.toLowerCase
  }

  def filterOpt(entityNameOpt: Option[String]): Boolean = {
    entityNameOpt match {
      case Some(entityName) => filter(entityName)
      case None => false
    }
  }

}

trait DatasetFilter extends EntityFilter {

  def filter(dataset: DatasetParams): Boolean

}

trait DatabaseConfigFilter extends EntityFilter {

  def filter(databaseConfig: DatabaseConfigParams): Boolean

}

trait ExperimentFilter extends EntityFilter {

  def filter(experimentParams: ExperimentParams): Boolean

}


case class TextFilter(text: String) extends DatasetFilter with DatabaseConfigFilter with ExperimentFilter {

  override val filterName: String = text

  override def filter(entityName: String): Boolean = {
    entityName.toLowerCase.contains(filterName.toLowerCase)
  }

  def filter(dataset: DatasetParams): Boolean = {
    filter(dataset.name)
  }

  def filter(databaseConfigParams: DatabaseConfigParams): Boolean = {
    filter(databaseConfigParams.name)
  }

  def filter(experimentParams: ExperimentParams): Boolean = {
    filter(experimentParams.name)
  }
}

case class DatabaseTypeFilter(store: String) extends DatabaseConfigFilter with ExperimentFilter {

  override val filterName: String = store

  def filter(databaseConfigParams: DatabaseConfigParams): Boolean = {
    filter(databaseConfigParams.dataStore.toString)
  }

  def filter(experimentParams: ExperimentParams): Boolean = {
    experimentParams.databaseConfigs.exists(config => filterOpt(config.storeType))
  }
}

case class DatasetNameFilter(name: String) extends DatabaseConfigFilter with ExperimentFilter {

  override val filterName: String = name

  def filter(databaseConfigParams: DatabaseConfigParams): Boolean = {
    filterOpt(databaseConfigParams.datasetDetails.datasetName)
  }

  def filter(experimentParams: ExperimentParams): Boolean = {
    experimentParams.databaseConfigs.exists(config => filterOpt(config.datasetName))
  }
}

case class ExperimentStateFilter(name: String) extends ExperimentFilter {

  override val filterName: String = name

  override def filter(experimentParams: ExperimentParams): Boolean = {
    filterOpt(experimentParams.experimentState.map(_.toString))
  }
}

object EntityFilter {
  implicit val textFilterFormat: Format[TextFilter] = Json.format[TextFilter]
  implicit val databaseTypeFilterFormat: Format[DatabaseTypeFilter] = Json.format[DatabaseTypeFilter]
  implicit val datasetNameFilterFormat: Format[DatasetNameFilter] = Json.format[DatasetNameFilter]
  implicit val experimentStateFilterFormat: Format[ExperimentStateFilter] = Json.format[ExperimentStateFilter]

  implicit val filtersReads: Reads[EntityFilter] = (json: JsValue) => {
    (json \ "type").validate[String].flatMap {
      case "TextFilter"               => json.validate[TextFilter]
      case "DatabaseTypeFilter"       => json.validate[DatabaseTypeFilter]
      case "ExperimentStateFilter"    => json.validate[ExperimentStateFilter]
      case "DatasetNameFilter"            => json.validate[DatasetNameFilter]
      case other                      => JsError(s"Unknown type: $other")
    }
  }

  implicit val filtersWrites: Writes[EntityFilter] = {
    case filter: TextFilter          => Json.toJson(filter)(textFilterFormat).as[JsObject] + ("type" -> JsString("TextFilter"))
    case filter: DatabaseTypeFilter  => Json.toJson(filter)(databaseTypeFilterFormat).as[JsObject] + ("type" -> JsString("DatabaseTypeFilter"))
    case filter: DatasetNameFilter   => Json.toJson(filter)(datasetNameFilterFormat).as[JsObject] + ("type" -> JsString("DatasetNameFilter"))
    case filter: ExperimentStateFilter     => Json.toJson(filter)(experimentStateFilterFormat).as[JsObject] + ("type" -> JsString("ExperimentStateFilter"))
  }

  implicit val filtersFormat: Format[EntityFilter] = Format(filtersReads, filtersWrites)
}