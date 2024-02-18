markdown
Copy code
## Setting up Perfix on Kubernetes (EKS) with Appropriate Permissions

### 1. Create an EKS Cluster
```bash
eksctl create cluster \
--name test-cluster \
--version 1.23 \
--region us-west-2 \
--nodegroup-name standard-workers \
--node-type t3.micro \
--nodes 2 \
--nodes-min 1 \
--nodes-max 3 \
--managed
```

### 2. Create an IAM OIDC Identity Provider for Your Cluster
Follow the instructions provided in the [AWS documentation](https://docs.aws.amazon.com/eks/latest/userguide/service_IAM_role.html) to create an IAM OIDC identity provider for your cluster.


### 3. Create an IAM Policy
Create an IAM policy with the necessary permissions for Perfix to access AWS resources like DynamoDB, S3, etc.
Example Policy:
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": "docdb:*",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "dynamodb:*",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "elasticache:*",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "rds-data:*",
            "Resource": "*"
        }
    ]
}
```

### 4. Create an IAM Role
Create an IAM role and attach the IAM policy created in the previous step to it.

### 5. Associate the IAM Role with the Kubernetes Service Account
Follow the instructions provided in the AWS documentation to associate the IAM role created in the previous step with the Kubernetes service account used by Perfix pods.
Example:
```
eksctl create iamserviceaccount \
--cluster=test-cluster \
--namespace=default \
--name=perfix-service-account \
--attach-policy-arn=arn:aws:iam::${ACCOUNT_ID}:policy/perfix-policy \
--approve
```

### 6. Deploy Perfix on Kubernetes
Use Kubernetes manifests or Helm charts to deploy Perfix on your EKS cluster.
```
helm install charts/ --generate-name
kubectl set serviceaccount deployment perfix-deployment perfix-service-account  
```