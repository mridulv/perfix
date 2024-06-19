package io.perfix.stores.redis

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.elasticache.model._
import com.amazonaws.services.elasticache.{AmazonElastiCache, AmazonElastiCacheClientBuilder}
import io.perfix.common.CommonConfig.DB_SUBNET_GROUP_NAME
import io.perfix.launch.StoreLauncher
import io.perfix.model.api.DatabaseState
import io.perfix.model.api.DatabaseState.DatabaseState
import io.perfix.model.store.DatabaseSetupParams

import java.util.concurrent.TimeUnit
import scala.util.Random

class RedisLauncher(override val databaseSetupParams: RedisDatabaseSetupParams)
  extends StoreLauncher {

  override def launch(): (DatabaseSetupParams, DatabaseState) = {
    val clusterId = "cluster" + Random.alphanumeric.take(5).mkString("")
    val cacheNodeType = databaseSetupParams.cacheNodeType.getOrElse("cache.t3.micro")
    val numCacheNodes = databaseSetupParams.numCacheNodes.getOrElse(1)

    val credentialsProvider = DefaultAWSCredentialsProviderChain.getInstance()

    val elasticacheClient = AmazonElastiCacheClientBuilder.standard()
      .withCredentials(credentialsProvider)
      .withRegion(Regions.US_WEST_2)
      .build()

    val createCacheClusterRequest = new CreateCacheClusterRequest()
      .withCacheClusterId(clusterId)
      .withCacheNodeType(cacheNodeType)
      .withEngine("redis")
      .withNumCacheNodes(numCacheNodes)
      .withPreferredAvailabilityZone("us-west-2b")
      .withCacheSubnetGroupName(DB_SUBNET_GROUP_NAME)

    try {
      elasticacheClient.createCacheCluster(createCacheClusterRequest)
      waitForCluster(elasticacheClient, clusterId)

      val describeRequest = new DescribeCacheClustersRequest()
        .withCacheClusterId(clusterId)
        .withShowCacheNodeInfo(true)
      val describeResponse = elasticacheClient.describeCacheClusters(describeRequest)
      val cacheNode = describeResponse.getCacheClusters.get(0).getCacheNodes.get(0)
      val endpoint = cacheNode.getEndpoint

      val updatedDatabaseSetupParams = databaseSetupParams.copy(
        dbDetails = Some(RedisConnectionParams(endpoint.getAddress, endpoint.getPort))
      )

      println(s"Redis cluster creation initiated: ${describeResponse.getCacheClusters.get(0).getCacheClusterId}")
      (updatedDatabaseSetupParams, DatabaseState.Created)
    } catch {
      case ex: Exception =>
        println(s"Error creating ElastiCache cluster: ${ex.getMessage}")
        (databaseSetupParams, DatabaseState.Failed)
    } finally {
      elasticacheClient.shutdown()
    }
  }

  def waitForCluster(elasticacheClient: AmazonElastiCache, clusterId: String): Boolean = {
    var isAvailable = false
    var attempts = 0
    val maxAttempts = 30

    while (!isAvailable && attempts < maxAttempts) {
      try {
        TimeUnit.SECONDS.sleep(20)
        val describeRequest = new DescribeCacheClustersRequest().withCacheClusterId(clusterId).withShowCacheNodeInfo(true)
        val describeResponse = elasticacheClient.describeCacheClusters(describeRequest)
        val clusterStatus = describeResponse.getCacheClusters.get(0).getCacheClusterStatus
        println("Waiting for the redis store to be up ...")

        if (clusterStatus == "available") {
          isAvailable = true
        }
      } catch {
        case e: Exception => e.printStackTrace()
      }
      attempts += 1
    }
    isAvailable
  }
}

object RedisLauncher {
  val CLUSTER_ID = "clusterId"
  val CACHE_NODE_TYPE = "cacheNodeType"
  val NUM_CACHE_NODES = "numCacheNodes"
}
