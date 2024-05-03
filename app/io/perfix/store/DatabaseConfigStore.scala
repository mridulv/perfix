package io.perfix.store

import com.google.inject.{Inject, Singleton}
import io.perfix.model.{DatabaseConfigId, DatabaseConfigParams}
import io.perfix.store.tables.DatabaseConfigTable
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

@Singleton
class DatabaseConfigStore @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  private val databaseConfigTable = TableQuery[DatabaseConfigTable]
  import dbConfig._
  import profile.api._

  def create(databaseConfigParams: DatabaseConfigParams): DatabaseConfigParams = unwrapFuture {
    db.run {
      val databaseConfigRow = databaseConfigParams.toDatabaseConfigRow
      (databaseConfigTable returning databaseConfigTable.map(_.id)
        into ((databaseConfigRow, id) => databaseConfigRow.copy(id=id))
        ) += databaseConfigRow
    }
  }.toDatabaseConfigParams

  def update(databaseConfigId: DatabaseConfigId,
             databaseConfigParams: DatabaseConfigParams): Int = unwrapFuture {
    db.run {
      val databaseConfigRow = databaseConfigParams.toDatabaseConfigRow
      val query = for { e <- databaseConfigTable if e.id === databaseConfigId.id } yield e.obj
      query.update(databaseConfigRow.obj)
    }
  }

  def get(databaseConfigId: DatabaseConfigId): Option[DatabaseConfigParams] = unwrapFuture {
    db.run {
      databaseConfigTable
        .filter(_.id === databaseConfigId.id)
        .result
        .headOption
        .map(_.map(_.toDatabaseConfigParams))
    }
  }

  def list(): Seq[DatabaseConfigParams] = unwrapFuture {
    db.run {
      databaseConfigTable.result.map(_.map(_.toDatabaseConfigParams))
    }
  }

  def delete(databaseConfigId: DatabaseConfigId): Int = unwrapFuture {
    db.run {
      databaseConfigTable.filter(_.id === databaseConfigId.id).delete
    }
  }


  private def unwrapFuture[A](f: Future[A]): A = {
    Await.result(f, Duration.Inf)
  }
}