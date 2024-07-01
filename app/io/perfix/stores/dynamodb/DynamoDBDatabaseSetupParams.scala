package io.perfix.stores.dynamodb

import io.perfix.model.store.DatabaseSetupParams
import io.perfix.query.PerfixQuery
import play.api.libs.json.{Format, Json}

case class DynamoDBDatabaseSetupParams(tableName: String,
                                       rcu: Long,
                                       wcu: Long,
                                       partitionKey: String,
                                       sortKey: String,
                                       gsiParams: Seq[DynamoDBGSIParam]) extends DatabaseSetupParams {

  var dynamoDBTableParams: Option[DynamoDBTableParams] = None
  var dynamoDBCapacityParams: Option[DynamoDBCapacityParams] = None
  var dynamoDBGSIMetadataParams: Option[DynamoDBGSIMetadataParams] = None

  def indexes(): Seq[DynamoDBIndex] = {
    val primaryIndex = dynamoDBTableParams.map(p => DynamoDBIndex(p.tableName, p.partitionKey, p.sortKey))
    val secondaryIndexes = dynamoDBGSIMetadataParams
      .map(_.gsiParams)
      .getOrElse(Seq.empty)
      .map(p => DynamoDBIndex(p.tableName, p.partitionKey, p.sortKey))

    Seq(primaryIndex).flatten ++ secondaryIndexes
  }

}

case class DynamoDBGSIMetadataParams(gsiParams: Seq[DynamoDBGSIParam])
case class DynamoDBGSIParam(partitionKey: String, sortKey: String) {

  def tableName: String = {
    s"gsi_${partitionKey}_${sortKey}"
  }

}

case class DynamoDBIndex(tableName: String, partitionKey: String, sortKey: String) {

  def validForFilters(perfixQuery: PerfixQuery): Boolean = {
    perfixQuery.selectFields.forall(e => partitionKey == e)
  }

}

case class DynamoDBTableParams(urlOpt: Option[String], tableName: String, partitionKey: String, sortKey: String)
case class DynamoDBCapacityParams(readCapacity: Option[Long], writeCapacity: Option[Long])

object DynamoDBDatabaseSetupParams {
  implicit val DynamoDBGSIParamFormatter: Format[DynamoDBGSIParam] = Json.format[DynamoDBGSIParam]
  implicit val DynamoDBStoreParamsFormatter: Format[DynamoDBDatabaseSetupParams] = Json.format[DynamoDBDatabaseSetupParams]
}

