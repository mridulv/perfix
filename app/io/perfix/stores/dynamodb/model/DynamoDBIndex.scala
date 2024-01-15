package io.perfix.stores.dynamodb.model

import io.perfix.query.PerfixQuery

case class DynamoDBIndex(tableName: String, partitionKey: String, sortKey: String) {

  def validForFilters(perfixQuery: PerfixQuery): Boolean = {
    perfixQuery.selectFields.forall(e => partitionKey == e)
  }

}
