package io.perfix.stores.redis

import io.perfix.exceptions.{InvalidStateException, WrongQueryException}
import io.perfix.model.{AddressType, ColumnDescription, NameType}
import io.perfix.model.api.{Dataset, DatasetParams}
import io.perfix.query.{DBQuery, NoSqlDBQuery, SqlDBQuery, SqlDBQueryBuilder}
import io.perfix.stores.DataStore
import redis.clients.jedis.JedisPool

class RedisStore(override val databaseConfigParams: RedisDatabaseSetupParams)
  extends DataStore {

  private var jedisPool: JedisPool = _

  override val kindOfQuery: String = DBQuery.NoSql

  def connectAndInitialize(): Unit = {
    (databaseConfigParams.dbDetails) match {
      case Some(param) =>
        jedisPool = new JedisPool(param.url, param.port)
        jedisPool.setMinIdle(100)
      case _ => throw InvalidStateException("Redis Connection Params Must be defined")
    }
  }

  override def putData(rows: Seq[Map[String, Any]]): Unit = {
    val jedis = jedisPool.getResource
    val keyColumn = databaseConfigParams.keyColumn
    val keyValues = scala.collection.mutable.ListBuffer.empty[String]
    rows.map { row =>
      val key = row(keyColumn).toString
      val value = row.map { case (k, v) =>
        s"$k:$v"
      }.mkString(",")
      keyValues.append(key)
      keyValues.append(value)
    }
    jedis.mset(keyValues.toList: _*)
    jedisPool.returnResource(jedis)
  }

  override def readData(dbQuery: DBQuery): Seq[Map[String, Any]] = {
    val noSqlDBQuery = dbQuery match {
      case noSqlDBQuery: NoSqlDBQuery => noSqlDBQuery
      case _: SqlDBQuery => throw WrongQueryException("Sql query not supported")
      case _: SqlDBQueryBuilder => throw WrongQueryException("Sql query not supported")
    }

    val jedis = jedisPool.getResource
    try {
      // Iterate over the filters and retrieve values from Redis
      val matchedValues = noSqlDBQuery.filters.flatMap { filter =>
        Option(jedis.get(filter.fieldValue.toString)).map { value =>
          val keyValuePairs = value.split(",").flatMap { entry =>
            val Array(key, value) = entry.split(":").map(_.trim)
            Some(key -> value)
          }.toMap
          keyValuePairs
        }
      }

      matchedValues
    } finally {
      jedisPool.returnResource(jedis)
    }
  }

  override def cleanup(): Unit = {
    jedisPool.getResource.flushAll()
  }
}
