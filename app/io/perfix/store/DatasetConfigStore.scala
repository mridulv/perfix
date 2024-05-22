package io.perfix.store

import com.google.inject.{Inject, Singleton}
import io.perfix.model.{DatasetId, DatasetParams}
import io.perfix.store.tables.DatasetConfigTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class DatasetConfigStore @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val datasetConfigTable = TableQuery[DatasetConfigTable]
  import dbConfig._
  import profile.api._

  def create(datasetParams: DatasetParams): DatasetParams = unwrapFuture {
    db.run {
      val datasetConfigRow = datasetParams
        .copy(createdAt = Some(System.currentTimeMillis()))
        .toDatasetConfigParams
      (datasetConfigTable returning datasetConfigTable.map(_.id)
        into ((datasetParams, id) => datasetParams.copy(id=id))
        ) += datasetConfigRow
    }
  }.toDatasetParams

  def update(datasetId: DatasetId, datasetParams: DatasetParams): Int = unwrapFuture {
    db.run {
      val datasetConfigRow = datasetParams.toDatasetConfigParams
      val query = for { e <- datasetConfigTable if e.id === datasetId.id } yield e.obj
      query.update(datasetConfigRow.obj)
    }
  }

  def get(datasetId: DatasetId): Option[DatasetParams] = unwrapFuture {
    db.run {
      datasetConfigTable.filter(_.id === datasetId.id).result.headOption.map(_.map(_.toDatasetParams))
    }
  }

  def list(): Seq[DatasetParams] = unwrapFuture {
    db.run {
      datasetConfigTable.result.map(_.map(_.toDatasetParams))
    }
  }

  def delete(datasetId: DatasetId): Int = unwrapFuture {
    db.run {
      datasetConfigTable.filter(_.id === datasetId.id).delete
    }
  }


  private def unwrapFuture[A](f: Future[A]): A = {
    Await.result(f, Duration.Inf)
  }
}