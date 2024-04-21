package io.perfix.stores

import io.perfix.launch.{AWSCloudParams, LaunchStoreForm}
import io.perfix.model.{DataDescription, DatasetParams}
import io.perfix.query.PerfixQuery
import io.perfix.forms.FormSeries

trait DataStore {
  def launch(awsCloudParams: AWSCloudParams): Option[LaunchStoreForm]
  def storeInputs(datasetParams: DatasetParams): FormSeries
  def connectAndInitialize(): Unit
  def putData(rows: Seq[Map[String, Any]]): Unit
  def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]]
  def cleanup(): Unit
}

object DataStore extends Enumeration {

  type DataStore = Value

  val MySQLStore = Value("mysql")
  val RedisStore = Value("redis")
  val DynamoDBStore = Value("dynamodb")
  val MongoDBStore = Value("mongodb")

}
