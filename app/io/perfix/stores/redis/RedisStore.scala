package io.perfix.stores.redis

import io.perfix.exceptions.{InvalidStateException, PerfixQueryException}
import io.perfix.launch.StoreLauncher
import io.perfix.model.api.DatasetParams
import io.perfix.query.PerfixQuery
import io.perfix.stores.DataStore
import redis.clients.jedis.JedisPool

class RedisStore(datasetParams: DatasetParams,
                 override val databaseConfigParams: RedisDatabaseConfigParams)
  extends DataStore[RedisDatabaseConfigParams] {

  private var jedisPool: JedisPool = _
  private val redisParams: RedisParams = RedisParams()

  override def launcher(): Option[StoreLauncher[RedisDatabaseConfigParams]] = {
    Some(new RedisLauncher(redisParams, databaseConfigParams))
  }

  def connectAndInitialize(): Unit = {
    (redisParams.redisConnectionParams) match {
      case Some(param) =>
        jedisPool = new JedisPool(param.url, param.port)
        jedisPool.setMinIdle(100)
      case _ => throw InvalidStateException("Redis Connection Params Must be defined")
    }
  }

  override def putData(rows: Seq[Map[String, Any]]): Unit = {
    val jedis = jedisPool.getResource
    val keyColumn = redisParams.redisTableParams.map(_.keyColumn).getOrElse(throw InvalidStateException("Key Column Must be defined"))
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
  }

  override def readData(perfixQuery: PerfixQuery): Seq[Map[String, Any]] = {
    val jedis = jedisPool.getResource
    val matchedValues = perfixQuery.filtersOpt.flatMap(_.headOption) match {
      case Some(filter) =>
        Option(jedis.get(filter.fieldValue.toString)).flatMap { v =>
          val value = v.split(",").flatMap { e =>
            val k = e.split(":").head
            val v = e.split(":").reverse.head
            if (perfixQuery.projectedFieldsOpt.getOrElse(List.empty).contains(k)) {
              Some(k -> v)
            } else {
              None
            }
          }.toMap
          Some(value)
        }
      case None => throw PerfixQueryException("For RedisStore, filter should be present")
    }
    jedisPool.returnResource(jedis)
    Seq(matchedValues).flatten
  }

  override def cleanup(): Unit = {
    jedisPool.getResource.flushAll()
  }
}
