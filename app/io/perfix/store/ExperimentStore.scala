package io.perfix.store

import com.google.inject.{Inject, Singleton}
import io.perfix.model.experiment.{ExperimentId, ExperimentParams}
import io.perfix.store.tables.ExperimentTable
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

  def create(experimentParams: ExperimentParams): ExperimentParams = unwrapFuture {
    db.run {
      val experimentRow = experimentParams
        .copy(createdAt = Some(System.currentTimeMillis()))
        .toExperimentRow
      (experiments returning experiments.map(_.id)
        into ((experiment, id) => experiment.copy(id=id))
        ) += experimentRow
    }
  }.toExperimentParams

  def update(experimentId: ExperimentId, experimentParams: ExperimentParams): Int = unwrapFuture {
    db.run {
      val experimentRow = experimentParams.toExperimentRow
      val query = for { e <- experiments if e.id === experimentId.id } yield e.obj
      query.update(experimentRow.obj)
    }
  }

  def get(experimentId: ExperimentId): Option[ExperimentParams] = unwrapFuture {
    db.run {
      experiments.filter(_.id === experimentId.id).result.headOption.map(_.map(_.toExperimentParams))
    }
  }

  def list(): Seq[ExperimentParams] = unwrapFuture {
    db.run {
      experiments.result.map(_.map(_.toExperimentParams))
    }
  }

  def delete(experimentId: ExperimentId): Int = unwrapFuture {
    db.run {
      experiments.filter(_.id === experimentId.id).delete
    }
  }


  private def unwrapFuture[A](f: Future[A]): A = {
    Await.result(f, Duration.Inf)
  }
}
