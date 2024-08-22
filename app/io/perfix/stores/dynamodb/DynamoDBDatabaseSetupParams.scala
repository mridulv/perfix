package io.perfix.stores.dynamodb

import io.perfix.model.store.{DatabaseConnectionParams, DatabaseLaunchParams, DatabaseSetupParams}
import play.api.libs.json.{Format, Json}

case class DynamoDBDatabaseSetupParams(tableName: String,
                                       rcu: Long,
                                       wcu: Long,
                                       partitionKey: String,
                                       sortKey: String,
                                       gsiParams: Option[Seq[DynamoDBGSIParam]]) extends DatabaseSetupParams {

  val dbDetails: Option[DatabaseConnectionParams] = None

  val dynamoDBTableParams: DynamoDBTableParams = DynamoDBTableParams(None, tableName, partitionKey, sortKey)
  val dynamoDBCapacityParams: DynamoDBCapacityParams = DynamoDBCapacityParams(Some(rcu), Some(wcu))
  val dynamoDBGSIMetadataParams: DynamoDBGSIMetadataParams = DynamoDBGSIMetadataParams(gsiParams.getOrElse(Seq.empty))

  def indexes(): Seq[DynamoDBIndex] = {
    val primaryIndex = DynamoDBIndex(tableName, partitionKey, sortKey)
    val secondaryIndexes = dynamoDBGSIMetadataParams.gsiParams
      .map(p => DynamoDBIndex(p.tableName, p.partitionKey, p.sortKey))
    Seq(primaryIndex) ++ secondaryIndexes
  }

  override def databaseLaunchParams: DatabaseLaunchParams = DynamoDBLaunchParams()

  override def update(databaseConfigDetails: Option[DatabaseConnectionParams]): DatabaseSetupParams = {
    this
  }
}

case class DynamoDBLaunchParams() extends DatabaseLaunchParams

case class DynamoDBGSIMetadataParams(gsiParams: Seq[DynamoDBGSIParam])
case class DynamoDBGSIParam(partitionKey: String, sortKey: String) {

  def tableName: String = {
    s"gsi_${partitionKey}_${sortKey}"
  }

}

case class DynamoDBIndex(tableName: String, partitionKey: String, sortKey: String)

case class DynamoDBTableParams(urlOpt: Option[String], tableName: String, partitionKey: String, sortKey: String)
case class DynamoDBCapacityParams(readCapacity: Option[Long], writeCapacity: Option[Long])

object DynamoDBDatabaseSetupParams {
  implicit val DynamoDBGSIParamFormatter: Format[DynamoDBGSIParam] = Json.format[DynamoDBGSIParam]
  implicit val DynamoDBStoreParamsFormatter: Format[DynamoDBDatabaseSetupParams] = Json.format[DynamoDBDatabaseSetupParams]
}

