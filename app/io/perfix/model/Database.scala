package io.perfix.model

import io.perfix.model.DatabaseCategory.DatabaseCategory
import io.perfix.model.api.DatabaseState.DatabaseState
import io.perfix.model.api.{DatabaseConfigParams, DatabaseFormInput, DatabaseState, Dataset, FormInput, FormInputs}
import io.perfix.model.store.DatabaseSetupParams
import io.perfix.model.store.StoreType.{DynamoDB, MongoDB, MySQL, Redis, StoreType}
import io.perfix.stores.DataStore
import io.perfix.stores.documentdb.{DocumentDBDatabaseSetupParams, DocumentDBLauncher, DocumentDBStore}
import io.perfix.stores.dynamodb.{DynamoDBDatabaseSetupParams, DynamoDBStore}
import io.perfix.stores.mysql.{MySQLDatabaseSetupParams, MySQLLauncher, MySQLStore}
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
      case configParams: MySQLDatabaseSetupParams => new MySQLLauncher(configParams).launch()
      case _: DynamoDBDatabaseSetupParams => (databaseConfigParams, DatabaseState.Created)
    }
  }

  def getStore(databaseConfigParams: DatabaseConfigParams, dataset: Dataset): DataStore = {
    databaseConfigParams.databaseSetupParams match {
      case storeParams: MySQLDatabaseSetupParams => new MySQLStore(dataset.params, storeParams)
      case storeParams: DynamoDBDatabaseSetupParams => new DynamoDBStore(dataset.params, storeParams)
      case storeParams: DocumentDBDatabaseSetupParams => new DocumentDBStore(storeParams)
      case storeParams: RedisDatabaseSetupParams => new RedisStore(storeParams)
    }
  }

  def findRelevantDatabaseFormInput(databaseType: StoreType): DatabaseFormInput = {
    databaseType match {
      case MySQL => MySQLDatabase.databaseFormInput
      case Redis => RedisDatabase.databaseFormInput
      case MongoDB => DocumentDBDatabase.databaseFormInput
      case DynamoDB => DynamoDBDatabase.databaseFormInput
    }
  }

  val RedisDatabase: Database = Database(
    name = Redis,
    databaseCategory = Seq(DatabaseCategory.AWS_NOSQL),
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
    databaseCategory = Seq(DatabaseCategory.AWS_RDBMS),
    databaseFormInput = DatabaseFormInput(
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
  )

  val DynamoDBDatabase: Database = Database(
    name = DynamoDB,
    databaseCategory = Seq(DatabaseCategory.AWS_NOSQL),
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
            FormInput("gsiParams", "List of GSIs", api.FormInputType(GSIType))
          )
        )
      )
    )
  )

  val DocumentDBDatabase: Database = Database(
    name = MongoDB,
    databaseCategory = Seq(DatabaseCategory.AWS_NOSQL),
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
}
