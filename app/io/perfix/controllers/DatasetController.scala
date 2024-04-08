package io.perfix.controllers

import com.google.inject.Inject
import io.perfix.common.DatasetManager
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
}
