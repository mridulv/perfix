package io.perfix.launch

import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2ClientBuilder}
import com.amazonaws.services.ec2.model.{AuthorizeSecurityGroupIngressRequest, DescribeSubnetsRequest, IpPermission, IpRange}
import com.amazonaws.services.eks.AmazonEKSClientBuilder
import com.amazonaws.services.eks.model.DescribeClusterRequest
import io.perfix.common.CommonConfig.IS_TRIAL_MODE
import io.perfix.model.QuestionType
import io.perfix.question.Question
import io.perfix.question.Question.QuestionLabel

import scala.jdk.CollectionConverters._

trait LaunchStoreQuestion extends Question {

  val credentials: AWSCloudParams
  val launchQuestionsMapping: Map[QuestionLabel, QuestionType]

  override lazy val mapping: Map[QuestionLabel, QuestionType] = if (IS_TRIAL_MODE) {
    Map.empty
  } else {
    launchQuestionsMapping
  }

  override lazy val shouldAsk: Boolean = credentials.launchDB

  protected def addIngressRules(storeSGId: String): Unit = {
    val eksClusterName = "new-test-cluster"
    val request = new DescribeClusterRequest().withName(eksClusterName)
    val eksClient = AmazonEKSClientBuilder.defaultClient().describeCluster(request)
    val ec2Client: AmazonEC2 = AmazonEC2ClientBuilder.defaultClient()

    val subnetIds = eksClient.getCluster.getResourcesVpcConfig.getSubnetIds
    val subnetDescribeRequest = new DescribeSubnetsRequest()
    subnetDescribeRequest.setSubnetIds(subnetIds)
    val res = ec2Client.describeSubnets(subnetDescribeRequest)

    val ipPermissions = res.getSubnets.asScala.map(_.getCidrBlock).map { block =>
      val ipRange = new IpRange()
      ipRange.setCidrIp(block)

      new IpPermission()
        .withIpProtocol("-1")
        .withFromPort(-1)
        .withToPort(-1)
        .withIpv4Ranges(ipRange)
    }.toList

    try {
      val ingressRequest: AuthorizeSecurityGroupIngressRequest = new AuthorizeSecurityGroupIngressRequest()
        .withGroupId(storeSGId)
        .withIpPermissions(ipPermissions: _*)

      ec2Client.authorizeSecurityGroupIngress(ingressRequest)
    } catch {
      case e: Exception => println(s"Error while adding ingress rules : ${e.getMessage}")
    }
  }

}
