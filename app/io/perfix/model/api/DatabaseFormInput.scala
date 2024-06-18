package io.perfix.model.api

import io.perfix.model._
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
          FormInput("cacheNodeType", "Node Type", FormInputType(StringType)),
          FormInput("numCacheNodes", "Number of Cache Nodes", FormInputType(IntType))
        )
      ),
      FormInputs(
        Seq(
          FormInput("keyColumn", "Select a Key Column", FormInputType(SingleColumnSelectorType))
        )
      )
    )
  )

  val MySQLDatabaseFormInput: DatabaseFormInput = DatabaseFormInput(
    Seq(
      FormInputs(
        Seq(
          FormInput("instanceType", "Instance Type", FormInputType(StringType)),
          FormInput("tableName", "TableName", FormInputType(StringType))
        )
      ),
      FormInputs(
        Seq(
          FormInput("primaryIndexColumn", "Primary Index", FormInputType(SingleColumnSelectorType)),
          FormInput("secondaryIndexesColumn", "Secondary index", FormInputType(MultiColumnSelectorType))
        )
      )
    )
  )

  val DynamoDBDatabaseFormInput: DatabaseFormInput = DatabaseFormInput(
    Seq(
      FormInputs(
        Seq(
          FormInput("tableName", "Table Name", FormInputType(StringType)),
          FormInput("rcu", "RCU", FormInputType(IntType)),
          FormInput("wcu", "WCU", FormInputType(IntType))
        )
      ),
      FormInputs(
        Seq(
          FormInput("partitionKey", "Partition Key", FormInputType(SingleColumnSelectorType)),
          FormInput("sortKey", "Sort Key", FormInputType(SingleColumnSelectorType)),
          FormInput("gsiParams", "List of GSIs", FormInputType(GSIType))
        )
      )
    )
  )

  val DocumentDBDatabaseFormInput: DatabaseFormInput = DatabaseFormInput(
    Seq(
      FormInputs(
        Seq(
          FormInput("instanceClass", "Instance Class", FormInputType(StringType)),
          FormInput("collectionName", "Collection Name", FormInputType(StringType))
        )
      ),
      FormInputs(
        Seq(
          FormInput("indices", "Indexes", FormInputType(MultiColumnSelectorType))
        )
      )
    )
  )

}
