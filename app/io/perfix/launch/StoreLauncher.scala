package io.perfix.launch

import com.amazonaws.services.ec2.model.{AuthorizeSecurityGroupIngressRequest, DescribeSubnetsRequest, IpPermission, IpRange}
import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2ClientBuilder}
import com.amazonaws.services.eks.AmazonEKSClientBuilder
import com.amazonaws.services.eks.model.DescribeClusterRequest
import io.perfix.model.api.DatabaseState.DatabaseState
import io.perfix.model.store.DatabaseSetupParams

import scala.jdk.CollectionConverters._

trait StoreLauncher {

  val databaseSetupParams: DatabaseSetupParams

  def launch(): (DatabaseSetupParams, DatabaseState)

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
