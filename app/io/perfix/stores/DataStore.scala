package io.perfix.stores

import io.perfix.launch.{AWSCloudParams, LaunchStoreQuestion}
import io.perfix.model.DataDescription
import io.perfix.query.PerfixQuery
import io.perfix.question.{Question, Questionnaire}

trait DataStore {
  def launch(awsCloudParams: AWSCloudParams): Option[LaunchStoreQuestion]
  def storeInputs(dataDescription: DataDescription): Questionnaire
  def connectAndInitialize(): Unit
  def putData(): Unit
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
