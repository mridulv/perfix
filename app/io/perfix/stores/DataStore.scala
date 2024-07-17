package io.perfix.stores

import io.perfix.launch.StoreLauncher
import io.perfix.model.store.DatabaseSetupParams
import io.perfix.query.{DBQuery, SqlDBQueryBuilder}

trait DataStore {

  val databaseConfigParams: DatabaseSetupParams
  def connectAndInitialize(): Unit
  def putData(rows: Seq[Map[String, Any]]): Unit
  def readData(dbQuery: DBQuery): Seq[Map[String, Any]]
  def cleanup(): Unit
}

