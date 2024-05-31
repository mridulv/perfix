package io.perfix.controllers

import com.google.inject.Inject
import io.perfix.auth.AuthenticationAction
import io.perfix.manager.DatasetManager
import io.perfix.model.{DatasetId, DatasetParams, EntityFilter, UserInfo}
import org.pac4j.core.profile.{ProfileManager, UserProfile}
import org.pac4j.play.PlayWebContext
import org.pac4j.play.scala.{Security, SecurityComponents}
import play.api.libs.json.{JsArray, JsError, JsSuccess, Json}
import play.api.mvc.{Action, AnyContent, Request, Results}
import play.mvc.Http.RequestBody

import scala.jdk.CollectionConverters._

@Inject
class DatasetController @Inject()(val controllerComponents: SecurityComponents,
                                  authenticationAction: AuthenticationAction,
                                  datasetManager: DatasetManager) extends Security[UserProfile] {
  def create = authenticationAction(parse.json) { request =>
    val datasetParams = request.body.as[DatasetParams]
    Results.Ok(Json.toJson(datasetManager.create(datasetParams)))
  }

  def get(datasetId: Int) = authenticationAction { request =>
      Results.Ok(Json.toJson(datasetManager.get(DatasetId(datasetId))))
  }

  def data(datasetId: Int) = authenticationAction { request =>
      Results.Ok(Json.toJson(datasetManager.data(DatasetId(datasetId))))
  }

  def update(datasetId: Int) = authenticationAction(parse.json) { request =>
      val datasetParams = request.body.as[DatasetParams]
      Results.Ok(Json.toJson(datasetManager.update(DatasetId(datasetId), datasetParams)))
  }

  def all = authenticationAction(parse.json) { request =>
      val filters = request.body.as[Seq[EntityFilter]]
      Results.Ok(Json.toJson(datasetManager.all(filters)))
  }

  def delete(datasetId: Int) = authenticationAction { request =>
      datasetManager.delete(DatasetId(datasetId))
      Results.Ok
  }
}
