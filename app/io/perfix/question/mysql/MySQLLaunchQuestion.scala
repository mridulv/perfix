package io.perfix.question.mysql

import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2Client, AmazonEC2ClientBuilder}
import com.amazonaws.services.ec2.model.{AuthorizeSecurityGroupIngressRequest, DescribeSubnetsRequest, IpPermission, IpRange}
import com.amazonaws.services.eks.{AmazonEKSClient, AmazonEKSClientBuilder}
import com.amazonaws.services.eks.model.DescribeClusterRequest
import com.amazonaws.services.rds.{AmazonRDS, AmazonRDSClientBuilder}
import com.amazonaws.services.rds.model.{CreateDBInstanceRequest, DBInstance, DescribeDBInstancesRequest}
import io.perfix.common.CommonConfig.DB_SUBNET_GROUP_NAME
import io.perfix.launch.{AWSCloudParams, LaunchStoreQuestion}
import io.perfix.model.{QuestionType, StringType}
import io.perfix.question.Question.QuestionLabel
import io.perfix.question.mysql.ConnectionParamsQuestion.{PASSWORD, USERNAME}
import io.perfix.question.mysql.MySQLLaunchQuestion._
import io.perfix.question.mysql.TableParamsQuestions.DBNAME
import io.perfix.stores.mysql.{MySQLConnectionParams, MySQLParams, MySQLTableParams}

import java.util.concurrent.TimeUnit
import scala.util.Random
import scala.jdk.CollectionConverters._

class MySQLLaunchQuestion(override val credentials: AWSCloudParams,
                          override val storeQuestionParams: MySQLParams) extends LaunchStoreQuestion {

  override val launchQuestionsMapping: Map[QuestionLabel, QuestionType] = Map(
    DBNAME -> QuestionType(StringType, isRequired = false),
    USERNAME -> QuestionType(StringType, isRequired = false),
    PASSWORD -> QuestionType(StringType, isRequired = false),
    INSTANCE_IDENTIFIER -> QuestionType(StringType, isRequired = false),
    INSTANCE_TYPE -> QuestionType(StringType, isRequired = false)
  )

  override def setAnswers(answers: Map[QuestionLabel, Any]): Unit = {
    val userName = "user" + Random.alphanumeric.take(10).mkString("")
    val defaultDbName = "db" + Random.alphanumeric.take(5).mkString("")
    val instanceName = "instance" + Random.alphanumeric.take(5).mkString("")
    val pwd = Random.alphanumeric.take(10).mkString("")
    val dbName = answers.get(DBNAME).map(_.toString).getOrElse(defaultDbName)
    val username = answers.get(USERNAME).map(_.toString).getOrElse(userName)
    val password = answers.get(PASSWORD).map(_.toString).getOrElse(pwd)
    val instanceIdentifier = answers.get(INSTANCE_IDENTIFIER).map(_.toString).getOrElse(instanceName)
    val instanceType = answers.get(INSTANCE_TYPE).map(_.toString).getOrElse("db.t3.micro")

    val credentialsProvider = if (credentials.useInstanceRole) {
      DefaultAWSCredentialsProviderChain.getInstance()
    } else {
      new AWSStaticCredentialsProvider(new AWSCredentials {
        override def getAWSAccessKeyId: String = credentials.accessKey.get
        override def getAWSSecretKey: String = credentials.accessSecret.get
      })
    }

    val rdsClient = AmazonRDSClientBuilder.standard()
      .withCredentials(credentialsProvider)
      .withRegion(Regions.US_WEST_2)
      .build()

    val createDBRequest = new CreateDBInstanceRequest()
      .withDBInstanceIdentifier(instanceIdentifier)
      .withDBInstanceClass(instanceType)
      .withEngine("mysql")
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
      val connectUrl = s"jdbc:mysql://${response.getEndpoint.getAddress}:${response.getEndpoint.getPort}/${response.getDBName}?user=${username}&password=${password}"
      storeQuestionParams.mySQLConnectionParams = Some(MySQLConnectionParams(connectUrl, username, password))
      storeQuestionParams.mySQLTableParams = Some(MySQLTableParams(dbName, s"test${Random.alphanumeric.take(5).mkString("")}"))
      println(s"RDS instance creation initiated: ${response.getDBInstanceIdentifier}")
    } catch {
      case ex: Exception =>
        println(s"Error creating RDS instance: ${ex.getMessage}")
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

object MySQLLaunchQuestion {
  val INSTANCE_IDENTIFIER = "instanceIdentifier"
  val INSTANCE_TYPE = "instanceType"
}
