package io.perfix.stores

import io.perfix.model.DataWithDescription
import io.perfix.query.PerfixQuery
import io.perfix.stores.mysql.MySQLQuestionnaire

trait DataStore {
  def storeInputs(dataWithDescription: DataWithDescription): MySQLQuestionnaire
  def connectAndInitialize(): Unit
  def putData(): Unit
  def convertQuery(perfixQuery: PerfixQuery): String
  def readData(query: String): Seq[Map[String, Any]]
}
