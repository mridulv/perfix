package io.perfix.question.documentdb

import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.regions.Regions
import com.amazonaws.services.docdb.{AmazonDocDB, AmazonDocDBClientBuilder}
import com.amazonaws.services.docdb.model._
import io.perfix.common.CommonConfig.DB_SUBNET_GROUP_NAME
import io.perfix.launch.{AWSCloudParams, LaunchStoreForm}
import io.perfix.model.{FormInputType, StringType}
import io.perfix.question.Form.FormInputName
import io.perfix.question.documentdb.DocumentDBConnectionParamsForm.{DATABASE, URL}
import io.perfix.question.documentdb.DocumentDBLaunchForm._
import io.perfix.stores.documentdb.{DocumentDBConnectionParams, DocumentDBParams, DocumentDBTableParams}

import java.util.concurrent.TimeUnit
import scala.annotation.tailrec
import scala.util.Random

class DocumentDBLaunchForm(override val credentials: AWSCloudParams,
                           override val formParams: DocumentDBParams) extends LaunchStoreForm {

  override val launchQuestionsMapping: Map[FormInputName, FormInputType] = Map(
    DATABASE -> FormInputType(StringType, isRequired = false),
    DB_CLUSTER_IDENTIFIER -> FormInputType(StringType, isRequired = false),
    MASTER_USERNAME -> FormInputType(StringType, isRequired = false),
    MASTER_PASSWORD -> FormInputType(StringType, isRequired = false),
    INSTANCE_CLASS -> FormInputType(StringType, isRequired = false),
    URL -> FormInputType(StringType, isRequired = false)
  )

  override def setAnswers(answers: Map[FormInputName, Any]): Unit = {
    val userName = "user" + Random.alphanumeric.take(10).mkString("")
    val pwd = Random.alphanumeric.take(10).mkString("")
    val defaultDbName = "db-" + Random.alphanumeric.take(10).mkString("")
    val clusterIdentifier = answers.get(DB_CLUSTER_IDENTIFIER).map(_.toString).getOrElse(defaultDbName)
    val masterUsername = answers.get(MASTER_USERNAME).map(_.toString).getOrElse(userName)
    val masterPassword = answers.get(MASTER_PASSWORD).map(_.toString).getOrElse(pwd)
    val instanceClass = answers.get(INSTANCE_CLASS).map(_.toString).getOrElse("db.t3.medium")
    val urlOpt = answers.get(URL).map(_.toString)
    val dbName = answers.get(DATABASE).map(_.toString).getOrElse("perfix"+Random.alphanumeric.take(10).mkString(""))

    val credentialsProvider = if (credentials.useInstanceRole) {
      DefaultAWSCredentialsProviderChain.getInstance()
    } else {
      new AWSStaticCredentialsProvider(new AWSCredentials {
        override def getAWSAccessKeyId: String = credentials.accessKey.get
        override def getAWSSecretKey: String = credentials.accessSecret.get
      })
    }

    val docDBClient = AmazonDocDBClientBuilder.standard()
      .withCredentials(credentialsProvider)
      .withRegion(Regions.US_WEST_2)
      .build()

    val createDBClusterRequest = new CreateDBClusterRequest()
      .withDBClusterIdentifier(clusterIdentifier)
      .withMasterUsername(masterUsername)
      .withMasterUserPassword(masterPassword)
      .withEngine("docdb")
      .withEngineVersion("4.0.0")
      .withDBClusterParameterGroupName("docdbdefault")
      .withAvailabilityZones("us-west-2b")
      .withDBSubnetGroupName(DB_SUBNET_GROUP_NAME)

    val createDBInstanceRequest = new CreateDBInstanceRequest()
      .withDBInstanceIdentifier(clusterIdentifier + "-instance")
      .withDBClusterIdentifier(clusterIdentifier)
      .withDBInstanceClass(instanceClass)
      .withEngine("docdb")
      .withAvailabilityZone("us-west-2b")

    try {
      docDBClient.createDBCluster(createDBClusterRequest)
      waitForClusterAvailability(docDBClient, clusterIdentifier)

      val instanceResponse = docDBClient.createDBInstance(createDBInstanceRequest)
      waitForInstanceAvailability(docDBClient, clusterIdentifier + "-instance")

      val describeClustersRequest = new DescribeDBClustersRequest().withDBClusterIdentifier(clusterIdentifier)
      val clusterResponse = docDBClient.describeDBClusters(describeClustersRequest).getDBClusters.get(0)

      val dbSG = clusterResponse.getVpcSecurityGroups.get(0)
      addIngressRules(dbSG.getVpcSecurityGroupId)

      println("Response is: " + clusterResponse.getEndpoint)

      val documentDBURL = s"mongodb://${masterUsername}:${masterPassword}@${clusterResponse.getEndpoint}:${clusterResponse.getPort}/"

      val connectionParams = DocumentDBConnectionParams(
        urlOpt.getOrElse(documentDBURL),
        dbName
      )
      val documentDBTableParams = DocumentDBTableParams(
        "collection" + Random.alphanumeric.take(5).mkString("")
      )

      formParams.documentDBConnectionParams = Some(connectionParams)
      formParams.documentDBTableParams = Some(documentDBTableParams)

      println(s"DocumentDB cluster creation initiated: ${clusterResponse.getDBClusterIdentifier}")
      println(s"DocumentDB instance creation initiated: ${instanceResponse.getDBInstanceIdentifier}")
    } catch {
      case ex: Exception =>
        println(s"Error creating DocumentDB cluster/instance: ${ex.getMessage}")
    } finally {
      docDBClient.shutdown()
    }
  }

  @tailrec
  private def waitForClusterAvailability(docDBClient: AmazonDocDB, clusterIdentifier: String): Unit = {
    val describeClustersRequest = new DescribeDBClustersRequest().withDBClusterIdentifier(clusterIdentifier)
    val cluster = docDBClient.describeDBClusters(describeClustersRequest).getDBClusters.get(0)
    if (cluster.getStatus != "available") {
      println("Waiting for DocumentDB cluster to become available...")
      TimeUnit.SECONDS.sleep(30)
      waitForClusterAvailability(docDBClient, clusterIdentifier)
    } else {
      println(s"DocumentDB cluster is now available: ${cluster.getDBClusterIdentifier}")
    }
  }

  @tailrec
  private def waitForInstanceAvailability(docDBClient: AmazonDocDB, instanceIdentifier: String): Unit = {
    val describeInstancesRequest = new DescribeDBInstancesRequest().withDBInstanceIdentifier(instanceIdentifier)
    val instance = docDBClient.describeDBInstances(describeInstancesRequest).getDBInstances.get(0)
    if (instance.getDBInstanceStatus != "available") {
      println("Waiting for DocumentDB instance to become available...")
      TimeUnit.SECONDS.sleep(30)
      waitForInstanceAvailability(docDBClient, instanceIdentifier)
    } else {
      println(s"DocumentDB instance is now available: ${instance.getDBInstanceIdentifier}")
    }
  }
}

object DocumentDBLaunchForm {
  val DB_CLUSTER_IDENTIFIER = "dbClusterIdentifier"
  val MASTER_USERNAME = "masterUsername"
  val MASTER_PASSWORD = "masterPassword"
  val INSTANCE_CLASS = "instanceClass"
}

