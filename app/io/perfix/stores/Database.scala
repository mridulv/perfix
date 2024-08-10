package io.perfix.stores

import io.perfix.model._
import io.perfix.model.api.DatabaseState.DatabaseState
import io.perfix.model.api._
import io.perfix.model.store.DatabaseSetupParams
import io.perfix.model.store.StoreType.{DynamoDB, MariaDB, MongoDB, MySQL, Postgres, Redis, StoreType}
import io.perfix.stores.documentdb.{DocumentDBDatabaseSetupParams, DocumentDBLauncher, DocumentDBStore}
import io.perfix.stores.dynamodb.{DynamoDBDatabaseSetupParams, DynamoDBStore}
import io.perfix.stores.mariadb.MariaDBStore
import io.perfix.stores.mysql.{MySQLStore, RDSDatabaseSetupParams, RDSLauncher}
import io.perfix.stores.postgres.PostgreSQLStore
import io.perfix.stores.redis.{RedisDatabaseSetupParams, RedisLauncher, RedisStore}
import play.api.libs.json.{Format, Json}

case class Database(name: StoreType,
                    databaseCategory: Seq[DatabaseCategory],
                    databaseFormInput: DatabaseFormInput)

object Database {
  implicit val DatabaseFormatter: Format[Database] = Json.format[Database]

  def launch(databaseConfigParams: DatabaseSetupParams): (DatabaseSetupParams, DatabaseState) = {
    databaseConfigParams match {
      case configParams: RedisDatabaseSetupParams => new RedisLauncher(configParams).launch()
      case configParams: DocumentDBDatabaseSetupParams => new DocumentDBLauncher(configParams).launch()
      case configParams: RDSDatabaseSetupParams => new RDSLauncher(configParams).launch()
      case _: DynamoDBDatabaseSetupParams => (databaseConfigParams, DatabaseState.Created)
    }
  }

  def getStore(databaseConfigParams: DatabaseConfigParams, datasetParams: DatasetParams): DataStore = {
    databaseConfigParams.databaseSetupParams match {
      case storeParams: RDSDatabaseSetupParams => {
        storeParams.databaseType.getOrElse(MySQL) match {
          case MySQL => new MySQLStore(datasetParams, storeParams)
          case Postgres => new PostgreSQLStore(datasetParams, storeParams)
          case MariaDB => new MariaDBStore(datasetParams, storeParams)
        }
      }
      case storeParams: DynamoDBDatabaseSetupParams => new DynamoDBStore(datasetParams, storeParams)
      case storeParams: DocumentDBDatabaseSetupParams => new DocumentDBStore(storeParams)
      case storeParams: RedisDatabaseSetupParams => new RedisStore(storeParams)
    }
  }

  def findRelevantDatabaseFormInput(databaseType: StoreType): DatabaseFormInput = {
    allDatabases.find(_.name == databaseType).getOrElse(throw new RuntimeException(s"Invalid Database Type: $databaseType")).databaseFormInput
  }


  val RDSDatabaseFormInput = DatabaseFormInput(
    Seq(
      FormInputs(
        Seq(
          FormInput("instanceType", "Instance Type", api.FormInputType(StringType)),
          FormInput("tableName", "TableName", api.FormInputType(StringType))
        )
      ),
      FormInputs(
        Seq(
          FormInput("primaryIndexColumn", "Primary Index", api.FormInputType(SingleColumnSelectorType)),
          FormInput("secondaryIndexesColumn", "Secondary index", api.FormInputType(MultiColumnSelectorType))
        )
      )
    )
  )

  val RedisDatabase: Database = Database(
    name = Redis,
    databaseCategory = Seq(AWS_NOSQL),
    databaseFormInput = DatabaseFormInput(
      Seq(
        FormInputs(
          Seq(
            FormInput("cacheNodeType", "Node Type", api.FormInputType(StringType)),
            FormInput("numCacheNodes", "Number of Cache Nodes", api.FormInputType(IntType))
          )
        ),
        FormInputs(
          Seq(
            FormInput("keyColumn", "Select a Key Column", api.FormInputType(SingleColumnSelectorType))
          )
        )
      )
    )
  )

  val MySQLDatabase: Database = Database(
    name = MySQL,
    databaseCategory = Seq(AWS_RDBMS),
    databaseFormInput = RDSDatabaseFormInput
  )

  val PostgresDatabase: Database = Database(
    name = Postgres,
    databaseCategory = Seq(AWS_RDBMS),
    databaseFormInput = RDSDatabaseFormInput
  )

  val MariaDBDatabase: Database = Database(
    name = MariaDB,
    databaseCategory = Seq(AWS_RDBMS),
    databaseFormInput = RDSDatabaseFormInput
  )

  val DynamoDBDatabase: Database = Database(
    name = DynamoDB,
    databaseCategory = Seq(AWS_NOSQL),
    databaseFormInput = DatabaseFormInput(
      Seq(
        FormInputs(
          Seq(
            FormInput("tableName", "Table Name", api.FormInputType(StringType)),
            FormInput("rcu", "RCU", api.FormInputType(IntType)),
            FormInput("wcu", "WCU", api.FormInputType(IntType))
          )
        ),
        FormInputs(
          Seq(
            FormInput("partitionKey", "Partition Key", api.FormInputType(SingleColumnSelectorType)),
            FormInput("sortKey", "Sort Key", api.FormInputType(SingleColumnSelectorType)),
            FormInput("gsiParams", "List of GSIs", api.FormInputType(GSIType, isRequired = false))
          )
        )
      )
    )
  )

  val DocumentDBDatabase: Database = Database(
    name = MongoDB,
    databaseCategory = Seq(AWS_NOSQL),
    databaseFormInput = DatabaseFormInput(
      Seq(
        FormInputs(
          Seq(
            FormInput("instanceClass", "Instance Class", api.FormInputType(StringType)),
            FormInput("collectionName", "Collection Name", api.FormInputType(StringType))
          )
        ),
        FormInputs(
          Seq(
            FormInput("indices", "Indexes", api.FormInputType(MultiColumnSelectorType))
          )
        )
      )
    )
  )

  lazy val allDatabases: Seq[Database] = Seq(
    RedisDatabase,
    MySQLDatabase,
    DocumentDBDatabase,
    DynamoDBDatabase,
    PostgresDatabase,
    MariaDBDatabase
  )
}
