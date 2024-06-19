package io.perfix.db

import com.google.inject.{Inject, Singleton}
import io.perfix.auth.UserContext
import io.perfix.exceptions.UserNotDefinedException
import io.perfix.model.UserInfo
import io.perfix.model.experiment.{ExperimentId, ExperimentParams, ExperimentState}
import io.perfix.db.tables.ExperimentTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class ExperimentStore @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val experiments = TableQuery[ExperimentTable]
  import dbConfig._
  import profile.api._

  def create(experimentParams: ExperimentParams): ExperimentParams = unwrapFuture { userInfo =>
    db.run {
      val experimentRow = experimentParams
        .copy(createdAt = Some(System.currentTimeMillis()))
        .copy(experimentState = Some(ExperimentState.Created))
        .toExperimentRow(userInfo)
      (experiments returning experiments.map(_.id)
        into ((experiment, id) => experiment.copy(id=id))
        ) += experimentRow
    }
  }.toExperimentParams

  def update(experimentId: ExperimentId, experimentParams: ExperimentParams): Int = unwrapFuture { userInfo =>
    db.run {
      val experimentRow = experimentParams.toExperimentRow(userInfo)
      val query = for {
        e <- experiments
        if e.id === experimentId.id && e.userEmail === userInfo.email
      } yield e.obj
      query.update(experimentRow.obj)
    }
  }

  def get(experimentId: ExperimentId): Option[ExperimentParams] = unwrapFuture { userInfo =>
    db.run {
      experiments
        .filter(_.id === experimentId.id)
        .filter(_.userEmail === userInfo.email)
        .result.headOption.map(_.map(_.toExperimentParams))
    }
  }

  def list(): Seq[ExperimentParams] = unwrapFuture { userInfo =>
    db.run {
      experiments.filter(_.userEmail === userInfo.email).result.map(_.map(_.toExperimentParams))
    }
  }

  def delete(experimentId: ExperimentId): Int = unwrapFuture { userInfo =>
    db.run {
      experiments.filter(_.userEmail === userInfo.email).filter(_.id === experimentId.id).delete
    }
  }

  private def unwrapFuture[A](f: UserInfo => Future[A]): A = {
    val userInfo = UserContext.getUser
    userInfo match {
      case Some(user) => Await.result(f(user), Duration.Inf)
      case None => throw UserNotDefinedException()
    }
  }
}
