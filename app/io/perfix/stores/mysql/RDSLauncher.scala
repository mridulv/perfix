package io.perfix.stores.mysql

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.rds.model.{CreateDBInstanceRequest, DBInstance, DescribeDBInstancesRequest}
import com.amazonaws.services.rds.{AmazonRDS, AmazonRDSClientBuilder}
import io.perfix.common.CommonConfig.DB_SUBNET_GROUP_NAME
import io.perfix.launch.StoreLauncher
import io.perfix.model.api.DatabaseState
import io.perfix.model.api.DatabaseState.DatabaseState
import io.perfix.model.store.DatabaseSetupParams
import io.perfix.model.store.StoreType.{MariaDB, MySQL, Postgres}

import java.util.concurrent.TimeUnit
import scala.util.Random

class RDSLauncher(override val databaseSetupParams: RDSDatabaseSetupParams)
  extends StoreLauncher {

  import com.typesafe.config.ConfigFactory
  private val restConfig = ConfigFactory.load("application.conf")
  private val useLocalDB = restConfig.getBoolean("use.local.db")

  override def launch(): (DatabaseSetupParams, DatabaseState) = {
    if (useLocalDB) {
      val connectUrl = "jdbc:mysql://localhost:3306/perfix"
      val username = "root"
      val password = "test12345"
      val dbName = "perfix"
      val tableName = "test"
      val updatedDatabaseSetupParams = databaseSetupParams.copy(
          dbDetails = Some(MySQLConnectionParams(connectUrl, username, password)),
          dbName = Some(dbName),
          tableName = tableName
        )
      (updatedDatabaseSetupParams, DatabaseState.Created)
    } else {
      actualLaunch()
    }
  }

  def actualLaunch(): (DatabaseSetupParams, DatabaseState) = {
    val userName = "user" + Random.alphanumeric.take(10).mkString("")
    val defaultDbName = "db" + Random.alphanumeric.take(5).mkString("")
    val instanceName = "instance" + Random.alphanumeric.take(5).mkString("")
    val pwd = Random.alphanumeric.take(10).mkString("")
    val dbName = defaultDbName
    val username = userName
    val password = pwd
    val instanceIdentifier = instanceName
    val instanceType = databaseSetupParams.instanceType
    val tableName = databaseSetupParams.tableName

    val credentialsProvider = DefaultAWSCredentialsProviderChain.getInstance()

    val rdsClient = AmazonRDSClientBuilder.standard()
      .withCredentials(credentialsProvider)
      .withRegion(Regions.US_WEST_2)
      .build()

    val createDBRequest = new CreateDBInstanceRequest()
      .withDBInstanceIdentifier(instanceIdentifier)
      .withDBInstanceClass(instanceType)
      .withEngine(databaseSetupParams.databaseType.getOrElse(MySQL).toString.toLowerCase)
      .withMasterUsername(username)
      .withMasterUserPassword(password)
      .withAllocatedStorage(20)
      .withDBName(dbName)
      .withAvailabilityZone("us-west-2b")
      .withDBSubnetGroupName(DB_SUBNET_GROUP_NAME)

    try {
      rdsClient.createDBInstance(createDBRequest)
      waitForInstance(rdsClient, instanceIdentifier)

      val describeDBInstancesRequest = new DescribeDBInstancesRequest().withDBInstanceIdentifier(instanceIdentifier)
      val response = rdsClient.describeDBInstances(describeDBInstancesRequest).getDBInstances.get(0)

      val dbSG = response.getVpcSecurityGroups.get(0)
      addIngressRules(dbSG.getVpcSecurityGroupId)

      println("Response is: " + response.getEndpoint)
      val databaseType = databaseSetupParams.databaseType.getOrElse(MySQL) match {
        case MySQL => "mysql"
        case MariaDB => "mariadb"
        case Postgres => "postgresql"
      }
      val connectUrl = s"jdbc:${databaseType}://${response.getEndpoint.getAddress}:${response.getEndpoint.getPort}/${response.getDBName}?user=${username}&password=${password}"

      val updatedDatabaseSetupParams = databaseSetupParams.copy(
        dbDetails = Some(MySQLConnectionParams(connectUrl, username, password)),
        tableName = tableName,
        dbName = Some(dbName)
      )

      println(s"RDS instance creation initiated: ${response.getDBInstanceIdentifier}")
      (updatedDatabaseSetupParams, DatabaseState.Created)
    } catch {
      case ex: Exception =>
        println(s"Error creating RDS instance: ${ex.getMessage}")
        (databaseSetupParams, DatabaseState.Failed)
    } finally {
      rdsClient.shutdown()
    }
  }

  def waitForInstance(rdsClient: AmazonRDS, instanceIdentifier: String): Option[DBInstance] = {
    var instance: Option[DBInstance] = None
    var status = ""
    val maxRetries = 30
    var retries = 0

    while (status != "available" && retries < maxRetries) {
      try {
        TimeUnit.SECONDS.sleep(20)
        val describeDBInstancesRequest = new DescribeDBInstancesRequest().withDBInstanceIdentifier(instanceIdentifier)
        val response = rdsClient.describeDBInstances(describeDBInstancesRequest)
        println("Waiting for the mysql store to be up ...")
        if (!response.getDBInstances.isEmpty) {
          val dbInstance = response.getDBInstances.get(0)
          status = dbInstance.getDBInstanceStatus
          instance = Some(dbInstance)
        }
      } catch {
        case e: Exception => e.printStackTrace()
      }
      retries += 1
    }

    instance
  }
}

object RDSLauncher {
  val INSTANCE_IDENTIFIER = "instanceIdentifier"
  val INSTANCE_TYPE = "instanceType"
}
