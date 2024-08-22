package io.perfix.stores

//import akka.actor.ActorSystem
//import akka.stream.Materializer
//import com.mongodb.client.MongoClients
//import io.cequence.openaiscala.domain.{ChatRole, ModelId}
//import io.cequence.openaiscala.domain.response.ChatCompletionResponse
//import io.cequence.openaiscala.service.OpenAIServiceFactory
//import io.perfix.generator.FakeDataGenerator
//import io.perfix.manager.{ConfigManager, DatabaseConfigManager}
//import io.perfix.model._
//import io.perfix.model.api.{ConversationMessage, DatabaseConfigDetails, DatabaseConfigId, DatabaseConfigParams, DatasetDetails, DatasetId, DatasetParams, Field, SqlQueries, SqlQuery, UseCaseParams}
//import io.perfix.model.experiment._
//import io.perfix.model.store._
//import io.perfix.query.{DbQueryFilter, NoSqlDBQuery, SqlDBQuery, SqlDBQueryBuilder}
//import io.perfix.stores.documentdb.{DocumentDBConnectionParams, DocumentDBDatabaseSetupParams}
//import io.perfix.stores.dynamodb.{DynamoDBDatabaseSetupParams, DynamoDBGSIParam}
//import io.perfix.stores.mysql.{MySQLConnectionParams, RDSDatabaseSetupParams}
//import io.perfix.stores.postgres.PostgreSQLStore
//import io.perfix.stores.redis.{RedisConnectionParams, RedisDatabaseSetupParams}
//import io.perfix.util.ConversationSystemPrompt
//import io.perfix.util.ConversationSystemPrompt.SystemConversationMessage
//import net.sf.jsqlparser.expression.Expression
//import net.sf.jsqlparser.parser.CCJSqlParserUtil
//import net.sf.jsqlparser.statement.Statement
//import net.sf.jsqlparser.statement.select.{PlainSelect, Select}
//import org.pac4j.core.util.serializer.JavaSerializer
//import org.pac4j.play.store.PlayCookieSessionStore.uncompressBytes
//import org.pac4j.play.store.ShiroAesDataEncrypter
//import play.api.libs.json.{Format, JsDefined, JsNull, JsValue, Json}
//
//import java.sql.DriverManager
//import java.util
//import java.util.{Base64, Map}
//import scala.collection.immutable.Map
//import scala.collection.mutable.ListBuffer
//import scala.concurrent.duration.Duration
//import scala.concurrent.{Await, ExecutionContext}
//import scala.jdk.CollectionConverters.CollectionHasAsScala
//import scala.util.Random
//
//object TestApp {
//
////  def main(args: Array[String]): Unit = {
////    val entityFilters: Seq[EntityFilter] = Seq(
////      TextFilter("as"),
////      DatabaseTypeFilter("mysql"),
////      DatasetNameFilter("da1"),
////      ExperimentStateFilter(ExperimentState.Created.name)
////    )
////    import com.typesafe.config.ConfigFactory
////    val restConfig = ConfigFactory.load("application.conf")
////    val useLocalDB = restConfig.getBoolean("use.local.db")
////    println(useLocalDB)
////
////    val a = Json.parse(Json.toJson(entityFilters).toString()).as[Seq[EntityFilter]]
////    val cols = Json.parse("[{\"columnName\":\"student_name\",\"columnType\":{\"type\":\"NameType\",\"isUnique\":true},\"columnValueDistribution\":{\"value\":\"John\",\"probability\":0.1}},{\"columnName\":\"student_address\",\"columnType\":{\"type\":\"AddressType\",\"isUnique\":false}}]").as[Seq[ColumnDescription]]
////    val experimentParams = ExperimentParams(
////      None,
////      name = s"exp-${Random.nextInt()}",
////      concurrentQueries = 10,
////      experimentTimeInSeconds = 5,
////      query = PerfixQuery(limitOpt = Some(100)),
////      databaseConfigId = DatabaseConfigId(-1),
////      experimentResult = None,
////      createdAt = Some(System.currentTimeMillis()),
////      experimentState = Some(ExperimentState.Created)
////    )
////    val datasetParams = DatasetParams(
////      id = None,
////      name = s"dataset-${Random.nextInt()}",
////      description = "some desc",
////      rows = 100,
////      columns = cols
////    )
////    val mysqlStoreParams = MySQLStoreParams(
////      instanceType = "db.t3.medium",
////      tableName = "test",
////      primaryIndexColumn = Some("student_name"),
////      secondaryIndexesColumn = None
////    )
////    val dynamoDBStoreParams = DynamoDBStoreParams(
////      tableName = "test",
////      rcu = 100L,
////      wcu = 100L,
////      partitionKey = "student_name",
////      sortKey = "student_name",
////      gsiParams = Seq(DynamoDBGSIParam("student_name", "student_address"))
////    )
////    val documentDBStoreParams = DocumentDBStoreParams(
////      instanceClass = "db.t3.medium",
////      collectionName = "test",
////      indices = Seq("student_name")
////    )
////    val redisStoreParams = RedisStoreParams(
////      cacheNodeType = Some("cache.t3.micro"),
////      numCacheNodes = Some(1),
////      keyColumn = "student_name"
////    )
////    val databaseConfig1 = DatabaseConfigParams(
////      name = "mysql-config",
////      dataStore = StoreType.MySQL,
////      datasetId = DatasetId(-1),
////      storeParams = mysqlStoreParams
////    )
////    val databaseConfig2 = DatabaseConfigParams(
////      name = "documentdb-config",
////      dataStore = StoreType.MongoDB,
////      datasetId = DatasetId(-1),
////      storeParams = documentDBStoreParams
////    )
////    val databaseConfig3 = DatabaseConfigParams(
////      name = "dynamoDB-config",
////      dataStore = StoreType.DynamoDB,
////      datasetId = DatasetId(-1),
////      storeParams = dynamoDBStoreParams
////    )
////    val databaseConfig4 = DatabaseConfigParams(
////      name = "redis-config",
////      dataStore = StoreType.Redis,
////      datasetId = DatasetId(-1),
////      storeParams = redisStoreParams
////    )
////
////    val strr = "{\n    \"name\": \"first\",\n    \"writeBatchSize\": 33,\n    \"experimentTimeInSeconds\": 33,\n    \"concurrentQueries\": 33,\n    \"query\": {\n        \"limitOpt\": 33\n    },\n    \"databaseConfigId\": {\n        \"id\": 14\n    }\n}"
////
////
////    val experimentExecutor = new SimplePerformanceExperiment[MySQLStoreParams](
////      new MySQLStore(datasetParams, mysqlStoreParams),
////      experimentParams,
////      dataset = datasetParams.dataset
////    )
////
////    println(Json.toJson(databaseConfig1).toString())
////    println(Json.toJson(databaseConfig2).toString())
////    println(Json.toJson(databaseConfig3).toString())
////    println(Json.toJson(databaseConfig4).toString())
////    println(Json.toJson(datasetParams).toString())
////    println(Json.toJson(experimentParams).toString())
////
////    experimentExecutor.init()
////    val result = experimentExecutor.run()
////    println(Json.toJson(experimentParams.copy(experimentResult = Some(result))).toString())
////    experimentExecutor.cleanup()
////  }
//
//  def main2(args: Array[String]): Unit = {
//    implicit val ec = ExecutionContext.global
//    implicit val materializer = Materializer(ActorSystem())
//
//    val service = OpenAIServiceFactory(
//      apiKey = "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"
//    )
//    val text = """Extract the name and mailing address from this email:
//                 |Dear Kelly,
//                 |It was great to talk to you at the seminar. I thought Jane's talk was quite good.
//                 |Thank you for the book. Here's my address 2111 Ash Lane, Crestview CA 92002
//                 |Best,
//                 |Maya
//             """.stripMargin
//
////    val fut = service.createCompletion(text).map(completion =>
////      service.createChatCompletion()
////      println(completion.choices.head.text)
////    )
//
////    val assistant = Await.result(service.createAssistant(ModelId.gpt_3_5_turbo_instruct), Duration.Inf)
////    service.createThreadMessage()
////
////    var conversationHistory: List[ChatCompletionRequest.Message] = List()
//
////    def sendMessage(userMessage: String): String = {
////      // Add user message to conversation history
////      conversationHistory = conversationHistory :+ ChatCompletionRequest.Message(role = "user", content = userMessage)
////
////      // Create the chat completion request
////      val request = ChatCompletito(
////        model = "gpt-4",
////        messages = conversationHistory
////      )
////
////      // Call the OpenAI API
////      val response: ChatCompletionResponse = service.createChatCompletion(request)
////      val assistantMessage = response.choices.head.message.content
////
////      // Add assistant message to conversation history
////      conversationHistory = conversationHistory :+ ChatCompletionRequest.Message(role = "assistant", content = assistantMessage)
////
////      assistantMessage
////    }
////
////    def getSummary: String = {
////      // Request a summary of the conversation
////      val summaryRequest = ChatCompletionRequest(
////        model = "gpt-4",
////        messages = conversationHistory :+ ChatCompletionRequest.Message(role = "system", content = "Please summarize the conversation with the recommended database, schema, and configuration.")
////      )
////
////      // Call the OpenAI API for the summary
////      val summaryResponse: ChatCompletionResponse = openai.createChatCompletion(summaryRequest)
////      summaryResponse.choices.head.message.content
////    }
//
//    //println(Await.result(fut, Duration.Inf))
//  }
//
//  def main(args: Array[String]): Unit = {
//    Class.forName("com.mysql.cj.jdbc.Driver").newInstance()
//    val connectUrl = "jdbc:mysql://localhost:3306/perfix"
//    val username = "root"
//    val password = "test12345"
//    val dbName = "perfix"
//    val tableName = "test"
//    val connection = DriverManager.getConnection(connectUrl, username, password)
//    val statement = connection.createStatement()
//    val result = statement.executeQuery("select * from test;")
//    println(result.getMetaData.getColumnCount)
//
//    //conversation()
//  }
//
//  private def confirmationAtEveryCheck(messages: Seq[ConversationMessage]): Seq[String] = {
//    val requiredKeys = Seq(
//      "schema",
//      "databaseType",
//      "queries",
//      "filteredRows",
//      "experiment_time_in_seconds",
//      "total_rows",
//      "concurrent_reads_rate",
//      "concurrent_writes_rate"
//    )
//    implicit val ec = ExecutionContext.global
//    implicit val materializer = Materializer(ActorSystem())
//    val service = OpenAIServiceFactory()
//    val conversationMessage = ConversationMessage(
//      ChatRole.System.toString(),
//      """Given the user inputs, can you create a json object of the response in this format: {"schema": [{"fieldName" : "$fieldName", "fieldType": "$fieldType"}], "databaseType": [$database1, $database2],  "queries": [{"query" : $query1}, {"query" : $query2}], "filteredRows": $filteredRows, "experiment_time_in_seconds": $experiment_time_in_seconds, "total_rows": $total_rows, "concurrent_reads_rate": $concurrent_reads_rate, "concurrent_writes_rate": $concurrent_writes_rate }. Note if there is no corresponding value defined by the user till now, leave the field as null and do not assign any default values. Also the response should be a valid json object""".stripMargin
//    )
//    val toBeSend = (Seq(ConversationSystemPrompt.SystemConversationMessage) ++ messages ++ Seq(conversationMessage)).map(_.toBaseMessage)
//    val response = Await.result(service.createChatCompletion(toBeSend), Duration.Inf)
//      .choices
//      .head
//      .message
//      .content
//    println(s"Side-System: $response")
//    try {
//      val json = Json.parse(response)
//      val fields = checkCompilation(json)
//      fields
//    } catch {
//      case e: Exception => {
//        println(e.toString)
//        requiredKeys
//      }
//    }
//  }
//
//  private def checkCompilation(jsValue: JsValue): Seq[String] = {
//    val requiredKeys = Seq(
//      "schema", "databaseType", "queries",
//      "filteredRows", "experiment_time_in_seconds",
//      "total_rows", "concurrent_reads_rate", "concurrent_writes_rate"
//    )
//
//    def isNonNull(key: String): Boolean = (jsValue \ key) match {
//      case JsDefined(JsNull) => false
//      case JsDefined(_) => true
//      case _ => false
//    }
//
//    requiredKeys.filterNot(isNonNull)
//  }
//
//  private def conversation(): Unit = {
//    implicit val ec = ExecutionContext.global
//    implicit val materializer = Materializer(ActorSystem())
//    val service = OpenAIServiceFactory()
//    val messages: ListBuffer[ConversationMessage] = ListBuffer.empty
//    var isConversationComplete = false
//    while (!isConversationComplete) {
//      println("User :")
//      val userResponse = scala.io.StdIn.readLine()
//      val userConversationMessage = ConversationMessage(ChatRole.User.toString(), userResponse)
//      messages.append(userConversationMessage)
//
//      val fields = confirmationAtEveryCheck(messages.toSeq)
//      println(s"Compilation Failure: ${fields.mkString(",")} are still empty. Need to ask user questions for filling the values for these fields")
//      val compilationMessage = ConversationMessage(ChatRole.Assistant.toString(), s"Compilation Failure: ${fields.mkString(",")} are still empty. Need to ask user questions for filling the values for these fields")
//      messages.append(compilationMessage)
//      isConversationComplete = fields.isEmpty
//
//      val overallConversation = Seq(ConversationSystemPrompt.SystemConversationMessage) ++ messages.toSeq
//      val response = Await.result(service.createChatCompletion(overallConversation.map(_.toBaseMessage)), Duration.Inf)
//        .choices
//        .head
//        .message
//        .content
//      println(s"System : ${response}")
//      messages.append(ConversationMessage(ChatRole.System.toString(), response))
//    }
//  }
//
//  def main(args: Array[String]): Unit = {
////    val a = "H4sIAAAAAAAAAHVUzW/URhR/m7DZkGxR0gooiFaRisqFeL+8theEIFm+EhYSET4kVCmZHc96J7E97ni8mRxAXODCAQ4FUQn1HyASR1BPIE6o7QEEFy4gqDgVzv2S2rHX+aCUkTzye3q/ee/3fm9m+S1kQw7D86iDtEhQVzuKwvZxFGRzzx883DL3uBd6DsOAy5B9GGHB+ARsFG1OwjZzbRnsPwDxyi/2q31IfRsEbAkQ1uenOelQFoX1kLdOsQXiCxgxmlbRbJpVy8RN3UY1y6zVyoZu4WLLNG0LC9iUYNdjMNFNS9dLllk19KqOrYrVRMiwLL1cLBm2JWA4wZwOCZ/mrEVdEipCW9cINai/QOyUln7/xDdD7v2/eiBzDgYRxip6ituEy2/hAsAHfDICPjnCmOOSct2lqiJ1tsa4oyVJNYYi0daCbl7N6QZqKSAt5+a2Y38/mnv5fR5Acvj6Y+CpMWWViynoxesv8fDtp9e6oJ1rIMw4WcXUmecxP4W82XfTvxPt/KkL2fURyDgKKV7XrPOjr2be7L7xJEYFAjaXSka5VKuWjEpRL1esWrVilirh/zcnpwQjHqLubIdw2qLEXh0lF/mONs6YS5D/ywi/+OzWn++Snmc7yI2IDFRjN/jIIwLyxzm1I3fkDOEeEtDnMoyUiByG1mmY+C78Pjx3q/jHb73QNwH9baUoZjZpQA6zyBd8ScCnjRhTiLMXZgSnvrO3AQNECuKHlPkJj88b0B8HRMghqd0XYk4DkVq5DuIU+V1TBv+oJQBia7uAHuInf8kmBQw4tEP82S6Vvi4VAYMt5FF3KXVnU2rZpFkCPvOSsE6xdsCJPUohT0AuoFhEPGaeT5j7RGinT04sZ6RW/2Hidi9k4koTVmlpApptIYJwT6Hgtivp/EVKXMx8RVnEBxdQYazuWAxPno1Ojkatqu4Up08dWyx1zMZZyzgTnluIjHnCpg45csxdkoQdOYonyWxd6vvCmjGKpXxPfliVH+RiBj58PGaI+PHgpes37t3Ve+OhWszH92gF3T1s13+csW9jICUI2PH+E3BIBpQjocQ7iETcm01r89VgSt8rv177+epXL9RwTa4M18roJEEnIq9J+OXl618MfvfySk9ckcp89e743v3yXxdzFVX+BAAA"
////    val dataEncrypter = new ShiroAesDataEncrypter
////    val javaSerializer = new JavaSerializer
////    println(uncompressBytes(dataEncrypter.decrypt(Base64.getDecoder.decode(a))))
////    val values = javaSerializer.deserializeFromBytes()
////    println(values)
////
////    val documentDb = RedisDatabaseSetupParams(cacheNodeType = None,
////      numCacheNodes = Some(1),
////      keyColumn = "key",
////      dbDetails = Some(RedisConnectionParams("url", 1))
////    )
////
////    println(Json.toJson(documentDb))
//
//
//    val mongoClient = MongoClients.create("mongodb://userYf6DmUeN9l:do9sDIMhTD@db-a9w4kp8yn7.cluster-crgcg68yejh4.us-west-2.docdb.amazonaws.com:27017/?replicaSet=rs0&readPreference=secondaryPreferred&retryWrites=false")
//    val mongoDatabase = mongoClient.getDatabase("perfixvKiFEzkvzX")
//    mongoDatabase.createCollection("collectioncNiWN")
//
//
//  }
//
//
//  def main44(args: Array[String]): Unit = {
//    val a = "{\"name\":\"exp-1642352123\",\"writeBatchSize\":100,\"experimentTimeInSeconds\":5,\"concurrentQueries\":10,\"query\":{\"limitOpt\":100},\"databaseConfigs\":[{\"databaseConfigId\":{\"id\":-1}}],\"experimentState\":\"Created\",\"createdAt\":1718377543628}"
//    val filters: Seq[EntityFilter] = Seq(
//      TextFilter("testing"),
//      SampleDatasetFilter(true)
//    )
//
//    println(Json.toJson(filters))
//
//
//    val mysqlStoreParams = RDSDatabaseSetupParams(
//      instanceType = "db.t3.medium",
//      tableName = "test",
//      primaryIndexColumn = Some("name"),
//      secondaryIndexesColumn = Some(Seq.empty),
//      dbName = None,
//      dbDetails = None,
//      databaseType = None
//    )
//    val dbConfigParams = DatabaseConfigParams(
//      None,
//      "n",
//      mysqlStoreParams,
//      StoreType.MySQL,
//      None,
//      DatasetDetails(DatasetId(-1))
//    )
//      val datasetParams = DatasetParams(
//        None,
//        name = s"dataset",
//        description = "desc",
//        100,
//        Some(Seq(ColumnDescription("student", NameType()), ColumnDescription("address", AddressType())))
//      )
//
//      val r = dbConfigParams.databaseSetupParams match {
//        case rdsDatabaseSetupParams: RDSDatabaseSetupParams =>
//          val columnDescription = datasetParams.columns.getOrElse(Seq.empty).headOption
//          columnDescription match {
//            case Some(column) => column.columnType.getValue match {
//              case str: String => s"select * from ${rdsDatabaseSetupParams.tableName} where ${column.columnName} = \"$str\""
//              case v: Any => s"select * from ${rdsDatabaseSetupParams.tableName} where ${column.columnName} = $v"
//            }
//            case None => s"select * from ${rdsDatabaseSetupParams.tableName}"
//          }
//        case _ => "select * from $replace_with_the_table_name"
//      }
//
//      println(r)
//    val queryLatencies = Seq(PercentileLatency(50, 100.0), PercentileLatency(90, 200.0))
//    val writeLatencies = Seq(PercentileLatency(50, 150.0), PercentileLatency(90, 250.0))
//    val result = SingleExperimentResult(
//      overallQueryTime = 10L,
//      overallWriteTimeTaken = 5L,
//      numberOfCalls = 20,
//      queryLatencies = queryLatencies,
//      writeLatencies = writeLatencies
//    )
//    var experimentParams = ExperimentParams(
//      None,
//      name = s"exp-${Random.nextInt()}",
//      concurrentQueries = 10,
//      experimentTimeInSeconds = 5,
//      dbQuery = SqlDBQueryBuilder(
//        projectedFieldsOpt = Some(List("field1", "field2")),
//        filtersOpt = Some(List(DbQueryFilter("key1", "value1"), DbQueryFilter("key2", "value2"))),
//        tableName = "table_name"
//      ),
//      databaseConfigs = Seq(DatabaseConfigDetails(DatabaseConfigId(-1)), DatabaseConfigDetails(DatabaseConfigId(-2))),
//      experimentResults = Some(
//        Seq(
//          ExperimentResultWithDatabaseConfigDetails(DatabaseConfigDetails(DatabaseConfigId(-1)), result),
//          ExperimentResultWithDatabaseConfigDetails(DatabaseConfigDetails(DatabaseConfigId(-2)), result)
//        )
//      ),
//      createdAt = Some(System.currentTimeMillis()),
//      experimentState = Some(ExperimentState.Created)
//    )
//    val sqlQueries = SqlQueries(queries = Seq(SqlQuery("SELECT IndexName FROM your_table_name WHERE StartTime >= $start_time AND EndTime <= $end_time")))
//    val sqlBuilder = sqlQueries.queries.map { query =>
//      val statement: Statement = CCJSqlParserUtil.parse(query.query)
//      statement match {
//        case selectStatement: Select =>
//          val selectBody = selectStatement.getSelectBody.asInstanceOf[PlainSelect]
//
//          // Extract projected fields
//          val projectedFields = selectBody.getSelectItems.asScala.map(_.toString).toList
//
//          // Extract filters
//          val whereClause: Option[Expression] = Option(selectBody.getWhere)
//          val filters = whereClause.map { expression =>
//            expression.toString.split("AND").map { condition =>
//              val Array(field, value) = condition.split("=").map(_.trim)
//              DbQueryFilter(field, value.replace("'", ""))
//            }.toList
//          }
//
//          // Create PerfixQuery object
//          SqlDBQueryBuilder(
//            filtersOpt = filters,
//            projectedFieldsOpt = Some(projectedFields),
//            tableName = "table_new"
//          )
//
//        case _ => throw new IllegalArgumentException("Unable to find parse query")
//      }
//    }.headOption.getOrElse(throw new IllegalArgumentException("Unable to get any query from response"))
//    println(sqlBuilder)
//    experimentParams = experimentParams.copy(dbQuery = SqlDBQuery("select * from sql_query"))
//    println(Json.toJson(experimentParams))
//    experimentParams = experimentParams.copy(dbQuery = NoSqlDBQuery(List(DbQueryFilter("key", "value2"), DbQueryFilter("key2", "value2"))))
//    println(Json.toJson(experimentParams))
//    println(Json.toJson(datasetParams))
//
//    val postgreSQLStore = new PostgreSQLStore(
//      datasetParams = datasetParams,
//      databaseConfigParams = mysqlStoreParams
//    )
//
//    val databaseConfigManager = new ConfigManager()
//    println(Json.toJson(databaseConfigManager.databases()))
//    println(Json.toJson(databaseConfigManager.categories()))
//
//    val fakeDataGenerator = new FakeDataGenerator
//    val data = fakeDataGenerator.generateData(datasetParams)
////
////    postgreSQLStore.connectAndInitialize()
////    postgreSQLStore.putData(data.data)
////    val res = postgreSQLStore.readData(
////      PerfixQuery(None, projectedFieldsOpt = Some(List("name")), limitOpt = Some(10))
////    )
////    println(res)
////    postgreSQLStore.cleanup()
//
//    val ba = "{\n    \"name\": \"GFGFG\",\n    \"dataStore\": \"DynamoDB\",\n    \"datasetDetails\": {\n        \"datasetId\": {\n            \"id\": 29\n        }\n    },\n    \"databaseSetupParams\": {\n        \"tableName\": \"GFG\",\n        \"rcu\": 44,\n        \"wcu\": 44,\n        \"partitionKey\": \"studentName\",\n        \"sortKey\": \"studentName\",\n        \"gsiParams\": [\n            {\n                \"partitionKey\": \"GFGF\",\n                \"sortKey\": \"GFG\"\n            },\n            {\n                \"partitionKey\": \"FG\",\n                \"sortKey\": \"GF\"\n            }\n        ],\n        \"type\": \"DynamoDB\"\n    },\n    \"databaseState\": \"NotStarted\"\n}"
//    println("test")
//    println(Json.toJson(Json.parse(ba).as[DatabaseConfigParams]))
////    val gg = DynamoDBDatabaseSetupParams(
////      tableName = "s",
////      rcu = 10,
////      wcu = 10,
////      partitionKey = "as",
////      sortKey = "as",
////      gsiParams = Seq(DynamoDBGSIParam("s", "as"))
////    )
////    println(Json.toJson(Json.parse(ba).as[DatabaseConfigParams].copy(databaseSetupParams = gg)))
//
//
//    println(Json.toJson(mysqlStoreParams))
//    //println(Json.toJson(dbConfigParams))
////    println(Json.toJson(experimentParams))
////    println(Json.toJson(result))
//    println(Json.toJson(dbConfigParams))
//
//    println(dbConfigParams.dataStore.toString.toLowerCase)
//  }
//
//}
//
//
