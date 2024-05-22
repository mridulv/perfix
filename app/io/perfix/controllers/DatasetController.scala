package io.perfix.controllers

import com.google.inject.Inject
import io.perfix.manager.DatasetManager
import io.perfix.model.{DatasetId, DatasetParams, EntityFilter}
import org.pac4j.core.profile.UserProfile
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.json.Json
import play.api.mvc.Results

@Inject
class DatasetController @Inject()(val controllerComponents: SecurityComponents,
                                  datasetManager: DatasetManager) extends Security[UserProfile] {
  def create = Secure("Google2Client") {
    Action(parse.json) { request =>
      val datasetParams = request.body.as[DatasetParams]
      Results.Ok(Json.toJson(datasetManager.create(datasetParams)))
    }
  }

  def get(datasetId: Int) = Secure("Google2Client") {
    Action { request =>
      Results.Ok(Json.toJson(datasetManager.get(DatasetId(datasetId))))
    }
  }

  def data(datasetId: Int) = Secure("Google2Client") {
    Action { request =>
      Results.Ok(Json.toJson(datasetManager.data(DatasetId(datasetId))))
    }
  }

  def update(datasetId: Int) = Secure("Google2Client") {
    Action(parse.json) { request =>
      val datasetParams = request.body.as[DatasetParams]
      Results.Ok(Json.toJson(datasetManager.update(DatasetId(datasetId), datasetParams)))
    }
  }

  def all = Secure("Google2Client") {
    Action(parse.json) { request =>
      val filters = request.body.as[Seq[EntityFilter]]
      Results.Ok(Json.toJson(datasetManager.all(filters)))
    }
  }

  def delete(datasetId: Int) = Secure("Google2Client") {
    Action { request =>
      datasetManager.delete(DatasetId(datasetId))
      Results.Ok
    }
  }
}
