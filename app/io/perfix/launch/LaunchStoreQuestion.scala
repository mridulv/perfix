package io.perfix.launch

import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2Client}
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.eks.AmazonEKSClient
import com.amazonaws.services.eks.model.DescribeClusterRequest
import io.perfix.common.EksConfig
import io.perfix.question.Question

trait LaunchStoreQuestion extends Question {

  val credentials: AWSCloudParams

  override val shouldAsk: Boolean = credentials.launchDB

  protected def createTransitGateway(vpcId: String): Unit = {
    val ec2Client = AmazonEC2Client.builder().withRegion(EksConfig.EKS_REGION).build()

    val eksClient = AmazonEKSClient.builder().withRegion(EksConfig.EKS_REGION).build()
    val clusterResponse = eksClient.describeCluster(new DescribeClusterRequest().withName(EksConfig.EKS_NAME))
    val eksVpcId = clusterResponse.getCluster.getResourcesVpcConfig.getVpcId

    val createTransitGatewayRequest = new CreateTransitGatewayRequest()
      .withDescription("TransitGateway")

    val createTransitGatewayResponse = ec2Client.createTransitGateway(createTransitGatewayRequest)
    val transitGatewayId = createTransitGatewayResponse.getTransitGateway.getTransitGatewayId

    val attachVpcRequest1 = new CreateTransitGatewayVpcAttachmentRequest()
      .withTransitGatewayId(transitGatewayId)
      .withVpcId(eksVpcId)

    val attachVpcRequest2 = new CreateTransitGatewayVpcAttachmentRequest()
      .withTransitGatewayId(transitGatewayId)
      .withVpcId(vpcId)

    ec2Client.createTransitGatewayVpcAttachment(attachVpcRequest1)
    ec2Client.createTransitGatewayVpcAttachment(attachVpcRequest2)

    createRouteForVpc(ec2Client, vpcId, transitGatewayId)
    createRouteForVpc(ec2Client, eksVpcId, transitGatewayId)
  }

  def createRouteForVpc(ec2Client: AmazonEC2, vpcId: String, transitGatewayId: String): Unit = {
    val createRouteRequest = new CreateRouteRequest()
      .withDestinationCidrBlock("0.0.0.0/0")
      .withTransitGatewayId(transitGatewayId)
      .withVpcEndpointId(vpcId)

    ec2Client.createRoute(createRouteRequest)
  }

}
