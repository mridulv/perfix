package io.perfix.stores

import io.perfix.launch.StoreLauncher
import io.perfix.model.store.DatabaseSetupParams
import io.perfix.query.SqlDBQueryBuilder

trait DataStore {

  val databaseConfigParams: DatabaseSetupParams
  def connectAndInitialize(): Unit
  def putData(rows: Seq[Map[String, Any]]): Unit
  def readData(dbQuery: SqlDBQueryBuilder): Seq[Map[String, Any]]
  def cleanup(): Unit
}

