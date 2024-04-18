package io.perfix.controllers

import com.google.inject.Inject
import io.perfix.manager.DatasetManager
import io.perfix.model.{DatasetId, DatasetParams}
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents, Results}

@Inject
class DatasetController @Inject()(val controllerComponents: ControllerComponents,
                                  datasetManager: DatasetManager) extends BaseController {
  def create = Action(parse.json) { request =>
    val datasetParams = request.body.as[DatasetParams]
    Results.Ok(Json.toJson(datasetManager.create(datasetParams)))
  }

  def get(datasetId: Int) = Action { request =>
    Results.Ok(Json.toJson(datasetManager.get(DatasetId(datasetId))))
  }

  def update(datasetId: Int) = Action(parse.json) { request =>
    val datasetParams = request.body.as[DatasetParams]
    Results.Ok(Json.toJson(datasetManager.update(DatasetId(datasetId), datasetParams)))
  }

  def all = Action { request =>
    Results.Ok(Json.toJson(datasetManager.all()))
  }
}