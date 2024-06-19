package io.perfix.model.api

import io.perfix.model.{api, _}
import io.perfix.model.store.StoreType.{DynamoDB, MongoDB, MySQL, Redis, StoreType}
import play.api.libs.json.{Format, Json}

case class DatabaseFormInput(forms: Seq[FormInputs])

object DatabaseFormInput {
  implicit val DatabaseFormInputFormatter: Format[DatabaseFormInput] = Json.format[DatabaseFormInput]

  def findRelevantDatabaseFormInput(databaseType: StoreType): DatabaseFormInput = {
    databaseType match {
      case MySQL => MySQLDatabaseFormInput
      case Redis => RedisDatabaseFormInput
      case MongoDB => DocumentDBDatabaseFormInput
      case DynamoDB => DynamoDBDatabaseFormInput
    }
  }

  val RedisDatabaseFormInput: DatabaseFormInput = DatabaseFormInput(
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

  val MySQLDatabaseFormInput: DatabaseFormInput = DatabaseFormInput(
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

  val DynamoDBDatabaseFormInput: DatabaseFormInput = DatabaseFormInput(
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

  val DocumentDBDatabaseFormInput: DatabaseFormInput = DatabaseFormInput(
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

}
