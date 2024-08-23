package io.perfix.conversations

import akka.actor.ActorSystem
import akka.stream.Materializer
import io.cequence.openaiscala.domain.ChatRole
import io.cequence.openaiscala.service.OpenAIServiceFactory
import io.perfix.manager.{DatabaseConfigManager, DatasetManager, ExperimentManager}
import io.perfix.model.api._
import io.perfix.model.experiment.{ExperimentId, ExperimentParams}
import io.perfix.model.store.StoreType.{MySQL, StoreType}
import io.perfix.model.store.{DatabaseSetupParams, StoreType}
import io.perfix.model._
import io.perfix.query.SqlDBQuery
import io.perfix.stores.documentdb.DocumentDBDatabaseSetupParams
import io.perfix.stores.dynamodb.DynamoDBDatabaseSetupParams
import io.perfix.stores.mysql.RDSDatabaseSetupParams
import io.perfix.stores.redis.RedisDatabaseSetupParams
import net.sf.jsqlparser.JSQLParserException
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.Select
import play.api.libs.json.Json

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

class UseCaseConversationParser(response: String) {
  implicit val ec = ExecutionContext.global
  implicit val materializer = Materializer(ActorSystem())
  type TableName = String

  def init(datasetManager: DatasetManager,
           databaseConfigManager: DatabaseConfigManager,
           experimentManager: ExperimentManager): (DatasetId, Seq[DatabaseConfigId], ExperimentId) = {

    val experimentRun = Json.parse(response).as[ExperimentRun]

    val experimentConfig = getExperimentConfig(experimentRun)
    val databaseTypes = getDatabaseType(experimentRun)
    val columnsDescriptions = getDatasetDetails(experimentRun)
    val sqlQuery = getQuery(columnsDescriptions, experimentRun)
    val tableName = sqlQuery.tableName(columnsDescriptions)

    val datasetParams: DatasetParams = DatasetParams(
      None,
      tableName,
      s"${tableName} generated via bot",
      rows = experimentConfig.rows,
      columns = Some(columnsDescriptions)
    )
    val datasetId = datasetManager.create(datasetParams)


    val databaseConfigIds: Seq[DatabaseConfigId] = databaseTypes.map { databaseType =>
      val databaseConfigParams = DatabaseConfigParams(
        name = tableName.concat("-").concat("db").concat("-").concat(databaseType.toString),
        databaseSetupParams = getDatabaseSetupParams(tableName, databaseType, columnsDescriptions),
        dataStore = databaseType,
        datasetDetails = DatasetDetails(datasetId, None)
      )
      databaseConfigManager.create(databaseConfigParams)
    }

    val experimentParams: ExperimentParams = ExperimentParams(
      experimentId = None,
      name = tableName.concat("-").concat("exp"),
      experimentTimeInSeconds = None,
      concurrentQueries = experimentConfig.reads_per_minute,
      dbQuery = sqlQuery,
      databaseConfigs = databaseConfigIds.map(DatabaseConfigDetails(_))
    )
    val experimentId = experimentManager.create(experimentParams)
    (datasetId, databaseConfigIds, experimentId)
  }


  private def getDatasetDetails(experimentRun: ExperimentRun): Seq[ColumnDescription]  = {
    experimentRun.schema.map { field =>
      val columnType: ColumnType = field.fieldType.toLowerCase match {
        case "string" => TextType()
        case "integer" => NumericType(None)
        case "boolean" => BooleanValueType()
        case "double" => NumericType(None)
        case "long" => NumericType(None)
      }
      ColumnDescription(field.fieldName, columnType)
    }
  }

  private def getExperimentConfig(experimentRun: ExperimentRun): ExperimentConfig = {
    ExperimentConfig(
      experimentRun.total_rows,
      experimentRun.concurrent_writes_rate,
      experimentRun.concurrent_reads_rate
    )
  }

  private def getQuery(columnsDescriptions: Seq[ColumnDescription] , experimentRun: ExperimentRun): SqlDBQuery = {
    val query = experimentRun.query
    var success = false
    var attempts = 0
    implicit val ec = ExecutionContext.global
    implicit val materializer = Materializer(ActorSystem())
    val service = OpenAIServiceFactory()
    var sqlQuery = SqlQuery(query).toSqlDBQuery
    val prompt = s"""Can we have the jinja variables name same as the column name on which the operation is applied in this sql query
                    |        "$query"
                    |        Response should just be a valid sql query with jinja variables""".stripMargin
    while (!success && attempts < 10) {
      attempts += 1
      try {
        val response = Await.result(service.createChatCompletion(Seq(ConversationMessage(ChatRole.System.toString(), prompt).toBaseMessage)), Duration.Inf)
          .choices
          .head
          .message
          .content
        println("query : " + response)
        sqlQuery = SqlDBQuery(response)
        CCJSqlParserUtil.parse(sqlQuery.resolve(columnsDescriptions.map { c => (c.columnName, c.columnType.getValue)}.toMap).sql).asInstanceOf[Select]
        success = true
      } catch {
        case e: JSQLParserException => Some(CompilationError("", s"Invalid sql query: $query"))
      }
    }
    sqlQuery
  }

  private def getDatabaseType(experimentRun: ExperimentRun): Seq[StoreType] = {
    implicit val ec = ExecutionContext.global
    implicit val materializer = Materializer(ActorSystem())
    val service = OpenAIServiceFactory()
    experimentRun.databaseType.flatMap { db =>
      val mapping = StoreType.values.map(_.toString)
      val prompt = s"""Map this value "${db}" to one of these values "${mapping.mkString(",")}"
                      |        Just output the mapped value""".stripMargin
      val response = Await.result(service.createChatCompletion(Seq(ConversationMessage(ChatRole.System.toString(), prompt).toBaseMessage)), Duration.Inf)
        .choices
        .head
        .message
        .content
      println(prompt)
      println(response)
      StoreType.fromString(response)
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
          gsiParams = None
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
