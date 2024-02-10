package io.perfix.stores.redis

import io.perfix.exceptions.{InvalidStateException, PerfixQueryException}
import io.perfix.launch.{AWSCloudParams, LaunchStoreQuestion}
import io.perfix.model.DataDescription
import io.perfix.query.PerfixQuery
import io.perfix.question.redis.RedisLaunchQuestion
import io.perfix.stores.DataStore
import redis.clients.jedis.JedisPool

class RedisStore extends DataStore {
  private var jedisPool: JedisPool = _
  private var dataDescription: DataDescription = _
  private val redisParams: RedisParams = RedisParams()

  override def actualLaunch(awsCloudParams: AWSCloudParams): Option[LaunchStoreQuestion] = {
    Some(new RedisLaunchQuestion(awsCloudParams, redisParams))
  }

  override def storeInputs(dataDescription: DataDescription): RedisQuestionnaire = {
    this.dataDescription = dataDescription
    new RedisQuestionnaire(redisParams)
  }

  def connectAndInitialize(): Unit = {
    (redisParams.redisConnectionParams) match {
      case Some(param) =>
        jedisPool = new JedisPool(param.url, param.port)
        jedisPool.setMinIdle(100)
      case _ => throw InvalidStateException("Redis Connection Params Must be defined")
    }
  }

  override def putData(): Unit = {
    val data = dataDescription.data
    val jedis = jedisPool.getResource
    val keyColumn = redisParams.redisTableParams.map(_.keyColumn).getOrElse(throw InvalidStateException("Key Column Must be defined"))
    data.map { row =>
      val key = row(keyColumn).toString
      val value = row.map { case (k, v) =>
        s"$k:$v"
      }.mkString(",")
      jedis.set(key, value)
    }
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
