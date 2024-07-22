package io.perfix.manager

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.openaiscala.domain.ChatRole
import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.perfix.model.{BooleanValueType, ColumnDescription, ColumnType, NumericType, TextType}
import io.perfix.model.api.{ConversationMessage, DatabaseConfigDetails, DatabaseConfigId, DatabaseConfigParams, DatasetDetails, DatasetId, DatasetParams, ExperimentConfig, Field, SqlQueries}
import io.perfix.model.experiment.{ExperimentId, ExperimentParams}
import io.perfix.model.store.{DatabaseSetupParams, StoreType}
import io.perfix.model.store.StoreType.StoreType
import io.perfix.query.{DbQueryFilter, SqlDBQuery, SqlDBQueryBuilder}
import io.perfix.stores.documentdb.DocumentDBDatabaseSetupParams
import io.perfix.stores.dynamodb.DynamoDBDatabaseSetupParams
import io.perfix.stores.mysql.RDSDatabaseSetupParams
import io.perfix.stores.redis.RedisDatabaseSetupParams
import play.api.libs.json.Json

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

class UseCaseConversationParser(conversationMessages: Seq[ConversationMessage]) {
  implicit val ec = ExecutionContext.global
  implicit val materializer = Materializer(ActorSystem())
  type TableName = String

  def init(datasetManager: DatasetManager,
           databaseConfigManager: DatabaseConfigManager,
           experimentManager: ExperimentManager): (DatasetId, DatabaseConfigId, ExperimentId) = {
    val experimentConfig = getExperimentConfig()
    val databaseType = getDatabaseType()
    val (columnsDescriptions, tableName)  = getDatasetDetails()
    val sqlQuery = getQuery(tableName)

    val datasetParams: DatasetParams = DatasetParams(
      None,
      tableName,
      s"${tableName} generated via bot",
      rows = experimentConfig.rows,
      columns = Some(columnsDescriptions)
    )
    val datasetId = datasetManager.create(datasetParams)

    val databaseConfigParams: DatabaseConfigParams = DatabaseConfigParams(
      name = tableName.concat("-").concat("db"),
      databaseSetupParams = getDatabaseSetupParams(tableName, databaseType, columnsDescriptions),
      dataStore = databaseType,
      datasetDetails = DatasetDetails(datasetId, None)
    )
    val databaseConfigId = databaseConfigManager.create(databaseConfigParams)

    val experimentParams: ExperimentParams = ExperimentParams(
      experimentId = None,
      name = tableName.concat("-").concat("exp"),
      experimentTimeInSeconds = experimentConfig.experiment_time_in_seconds,
      concurrentQueries = experimentConfig.reads_per_minute,
      dbQuery = sqlQuery,
      databaseConfigs = Seq(DatabaseConfigDetails(databaseConfigId))
    )
    val experimentId = experimentManager.create(experimentParams)
    (datasetId, databaseConfigId, experimentId)
  }


  private def getDatasetDetails(): (Seq[ColumnDescription], TableName)  = {
    val service = OpenAIServiceFactory()
    val datasetConversation = ConversationMessage(
      ChatRole.System.toString(),
      """can you define in a single json array in this format: [{"fieldName" : "$fieldName", "fieldType": "$fieldType"}] fieldtype should be among string,integer,boolean,double and the response should be a valid json array. Make sure the response is just json array in the format defined without any other text.""".stripMargin
    )
    val allMessagesWithDatasetDetails = conversationMessages ++ Seq(datasetConversation)
    val response = Await.result(service.createChatCompletion(allMessagesWithDatasetDetails.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content
    val fields = Json.parse(response).as[Seq[Field]]

    val tableConversation = ConversationMessage(
      ChatRole.System.toString(),
      """if i have to name a table name in the database for the experiment, can you give a simple easy name. Name can contain multiple words but they should be combined with "_". Just give the name of the table as the response without anything else.""".stripMargin
    )
    val allMessagesWithTableName = conversationMessages ++ Seq(tableConversation)
    val tableName = Await.result(service.createChatCompletion(allMessagesWithTableName.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content

    val columnsDescriptions = fields.map { field =>
      val columnType: ColumnType = field.fieldType match {
        case "string" => TextType()
        case "integer" => NumericType(None)
        case "boolean" => BooleanValueType()
        case "double" => NumericType(None)
        case "long" => NumericType(None)
      }
      ColumnDescription(field.fieldName, columnType)
    }
    (columnsDescriptions, tableName.replace("\"", ""))
  }

  private def getExperimentConfig(): ExperimentConfig = {
    val service = OpenAIServiceFactory()
    val experimentConfigConversation = ConversationMessage(
      ChatRole.System.toString(),
      """can you return the number of rows, number of writes per minute, number of reads per minute, duration of the experiment in a json object in this json object: {"rows" : "$rows", "experiment_time_in_seconds" : "$num_rows", "num_writes_per_minute" : "$writes_per_minute", "reads_per_minute" : "$reads_per_minute"}. in the json object all the fields are either long or int. Make sure to return just json object and no other text""".stripMargin
    )
    val allMessagesWithDatasetDetails = conversationMessages ++ Seq(experimentConfigConversation)
    val response = Await.result(service.createChatCompletion(allMessagesWithDatasetDetails.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content

    Json.parse(response).as[ExperimentConfig]
  }

  private def getQuery(tableName: String): SqlDBQuery = {
    val service = OpenAIServiceFactory()
    val databaseConversation = ConversationMessage(
      ChatRole.System.toString(),
      """can you return the query which the user is interested in running in simple sql format {"queries" : [{"query" : $query1}, {"query" : $query2}]}. Don't return anything apart from this json array. Also make sure the variable names start with {{ and ends with }} like it is a jinja variable and have the same exact names as the column name on which the operation is applied. Also make sure the table name referenced in SQL is """ + tableName + """."""
    )
    val allMessages = conversationMessages ++ Seq(databaseConversation)
    val response = Await.result(service.createChatCompletion(allMessages.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content
    val sqlQueries = Json.parse(response).as[SqlQueries]
    sqlQueries.queries.headOption.getOrElse(throw new IllegalArgumentException("Unable to get any query from response")).toSqlDBQuery
  }

  private def getDatabaseType(): StoreType = {
    val service = OpenAIServiceFactory()
    val databaseConversation = ConversationMessage(
      ChatRole.System.toString(),
      """can you return in a string different databases selected by the user. Options should always be within options among "RDS-mysql","RDS-postgresql","RDS-mariadb","dynamodb","redis","memcache","documentdb". If there is an option beyond these do not return that. Also in case there are multiple return multiple of these segregated by commas. Eg RDS-mysql,RDS-postgresql."""
    )
    val allMessages = conversationMessages ++ Seq(databaseConversation)
    val response = Await.result(service.createChatCompletion(allMessages.map(_.toBaseMessage)), Duration.Inf)
      .choices
      .head
      .message
      .content

    StoreType.fromString(response) match {
      case Some(storeType) => storeType
      case None => throw new IllegalArgumentException("Unable to get database from response")
    }
  }

  private def getDatabaseSetupParams(tableName: TableName,
                             storeType: StoreType,
                             columnDescriptions: Seq[ColumnDescription]): DatabaseSetupParams = {
    storeType match {
      case StoreType.MySQL => RDSDatabaseSetupParams.defaultRDSDatabaseSetupParams(tableName, storeType)
      case StoreType.Postgres => RDSDatabaseSetupParams.defaultRDSDatabaseSetupParams(tableName, storeType)
      case StoreType.MariaDB => RDSDatabaseSetupParams.defaultRDSDatabaseSetupParams(tableName, storeType)
      case StoreType.DynamoDB => {
        val topColumn = columnDescriptions.map(_.columnName).head
        DynamoDBDatabaseSetupParams(
          tableName,
          rcu = 10,
          wcu = 10,
          partitionKey = topColumn,
          sortKey = topColumn,
          gsiParams = Seq.empty
        )
      }
      case StoreType.Redis => {
        val topColumn = columnDescriptions.map(_.columnName).head
        RedisDatabaseSetupParams(None, None, keyColumn = topColumn)
      }
      case StoreType.MongoDB => DocumentDBDatabaseSetupParams(collectionName = tableName, indices = Seq.empty)
    }
  }

}
