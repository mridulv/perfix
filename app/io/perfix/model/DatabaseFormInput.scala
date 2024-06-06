package io.perfix.model

import io.perfix.model.store.StoreType.{DynamoDBStoreType, MongoDBStoreType, MySQLStoreType, RedisStoreType, StoreType}
import play.api.libs.json.{Format, Json}

case class DatabaseFormInput(forms: Seq[FormInputs])

object DatabaseFormInput {
  implicit val DatabaseFormInputFormatter: Format[DatabaseFormInput] = Json.format[DatabaseFormInput]

  def findRelevantDatabaseFormInput(databaseType: StoreType): DatabaseFormInput = {
    databaseType match {
      case MySQLStoreType => MySQLDatabaseFormInput
      case RedisStoreType => RedisDatabaseFormInput
      case MongoDBStoreType => DocumentDBDatabaseFormInput
      case DynamoDBStoreType => DynamoDBDatabaseFormInput
    }
  }

  val RedisDatabaseFormInput = DatabaseFormInput(
    Seq(
      FormInputs(
        Map(
          "Node Type" -> FormInputType(StringType),
          "Number of Cache Nodes" -> FormInputType(IntType)
        )
      ),
      FormInputs(
        Map(
          "Select a Key Column" -> FormInputType(StringType)
        )
      )
    )
  )

  val MySQLDatabaseFormInput = DatabaseFormInput(
    Seq(
      FormInputs(
        Map(
          "Instance Type" -> FormInputType(StringType),
          "TableName" -> FormInputType(StringType)
        )
      ),
      FormInputs(
        Map(
          "Primary Index" -> FormInputType(StringType),
          "Secondary index" -> FormInputType(StringType)
        )
      )
    )
  )

  val DynamoDBDatabaseFormInput = DatabaseFormInput(
    Seq(
      FormInputs(
        Map(
          "RCU" -> FormInputType(IntType),
          "WCU" -> FormInputType(IntType)
        )
      ),
      FormInputs(
        Map(
          "List of GSIs" -> FormInputType(StringType)
        )
      )
    )
  )

  val DocumentDBDatabaseFormInput = DatabaseFormInput(
    Seq(
      FormInputs(
        Map(
          "Instance Class" -> FormInputType(StringType),
          "Collection Name" -> FormInputType(StringType)
        )
      ),
      FormInputs(
        Map(
          "Indexes" -> FormInputType(StringType)
        )
      )
    )
  )

}