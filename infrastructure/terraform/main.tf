# KMS
module "kms_key_lambda" {
  source                  = "cloudposse/kms-key/aws"
  version                 = "0.12.1"
  namespace               = var.globals["namespace"]
  stage                   = var.globals["stage"]
  name                    = "${var.globals["namespace"]}-${var.globals["stage"]}-lambdas"
  description             = "KMS key for lambdas"
  deletion_window_in_days = 30
  enable_key_rotation     = true
  alias                   = "alias/${var.globals["namespace"]}/${var.globals["stage"]}/lambda"
}

module "kms_key_parameter_store" {
  source                  = "cloudposse/kms-key/aws"
  version                 = "0.12.1"
  namespace               = var.globals["namespace"]
  stage                   = var.globals["stage"]
  name                    = "${var.globals["namespace"]}-${var.globals["stage"]}-parameter-store"
  description             = "KMS key for parameter store"
  deletion_window_in_days = 30
  enable_key_rotation     = true
  alias                   = "alias/${var.globals["namespace"]}/${var.globals["stage"]}/ps"
}

module "kms_key_s3" {
  source                  = "cloudposse/kms-key/aws"
  version                 = "0.12.1"
  namespace               = var.globals["namespace"]
  stage                   = var.globals["stage"]
  name                    = "${var.globals["namespace"]}-${var.globals["stage"]}-bucket"
  description             = "KMS key for bucket s3"
  deletion_window_in_days = 30
  enable_key_rotation     = true
  alias                   = "alias/${var.globals["namespace"]}/${var.globals["stage"]}/s3"
}

# VPC
module "vpc" {
  source                 = "terraform-aws-modules/vpc/aws"
  name                   = "${var.globals["namespace"]}-${var.globals["stage"]}"
  cidr                   = var.vpc["cidr"]
  azs                    = var.vpc["azs"]
  private_subnets        = var.vpc["private_subnets"]
  public_subnets         = var.vpc["public_subnets"]
  database_subnets       = var.vpc["database_subnets"]
  enable_nat_gateway     = var.vpc["enable_nat_gateway"]
  single_nat_gateway     = var.vpc["single_nat_gateway"]
  one_nat_gateway_per_az = var.vpc["one_nat_gateway_per_az"]
  enable_vpn_gateway     = var.vpc["enable_vpn_gateway"]
  enable_dns_hostnames   = var.vpc["enable_dns_hostnames"]
}

# MQ
module "mq_broker" {
  source                       = "cloudposse/mq-broker/aws"
  version                      = "2.0.1"
  namespace                    = var.globals["namespace"]
  stage                        = var.globals["stage"]
  name                         = var.globals["shortname"]
  apply_immediately            = var.mq_broker["apply_immediately"]
  auto_minor_version_upgrade   = var.mq_broker["auto_minor_version_upgrade"]
  deployment_mode              = var.mq_broker["deployment_mode"]
  engine_type                  = var.mq_broker["engine_type"]
  engine_version               = var.mq_broker["engine_version"]
  host_instance_type           = var.mq_broker["host_instance_type"]
  publicly_accessible          = var.mq_broker["publicly_accessible"]
  general_log_enabled          = var.mq_broker["general_log_enabled"]
  audit_log_enabled            = var.mq_broker["audit_log_enabled"]
  encryption_enabled           = var.mq_broker["encryption_enabled"]
  use_aws_owned_key            = var.mq_broker["use_aws_owned_key"]
  vpc_id                       = module.vpc.vpc_id
  subnet_ids                   = [module.vpc.public_subnets[0]]
  use_existing_security_groups = var.mq_broker["use_existing_security_groups"]
  allowed_security_group_ids   = [module.vpc.default_security_group_id]
  allowed_ingress_ports        = var.mq_broker["allowed_ingress_ports"]
  additional_security_group_rules = [
    {
      type        = "ingress"
      from_port   = 0
      to_port     = 65535
      protocol    = "-1"
      cidr_blocks = ["0.0.0.0/0"]
    }
  ]
}

resource "aws_ssm_parameter" "ssm_param_broker_id" {
  name      = "/mq/mq_broker_id"
  type      = "String"
  value     = module.mq_broker.broker_id
  overwrite = true
}

resource "aws_ssm_parameter" "ssm_param_broker_endpoint" {
  name      = "/mq/mq_broker_endpoint"
  type      = "String"
  value     = "amqps://${module.mq_broker.broker_id}.mq.sa-east-1.amazonaws.com"
  overwrite = true
}


resource "aws_ssm_parameter" "ssm_param_mq_broker_dashboard" {
  name      = "/mq/mq_broker_dashboard"
  type      = "String"
  value     = module.mq_broker.primary_console_url
  overwrite = true
}

#### EC2
resource "aws_iam_role" "ec2_instance" {
  name               = "web_iam_role"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_iam_role_policy" "ec2_instance" {
  name   = "${var.globals["namespace"]}-${var.globals["stage"]}-ec2"
  role   = aws_iam_role.ec2_instance.id
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchGetImage",
        "ecr:GetDownloadUrlForLayer"
      ],
      "Effect": "Allow",
      "Resource": "*"
    }
  ]
}
EOF
}

resource "aws_iam_instance_profile" "ec2_instance" {
  name = "${var.globals["namespace"]}-${var.globals["stage"]}-ec2"
  role = aws_iam_role.ec2_instance.name
}

data "aws_ami" "amazon_linux_2" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-ebs"]
  }
}

module "ec2_instance" {
  source                      = "cloudposse/ec2-instance/aws"
  version                     = "0.47.1"
  ami                         = data.aws_ami.amazon_linux_2.id
  vpc_id                      = module.vpc.vpc_id
  ssh_key_pair                = var.ec2["ssh_key_pair"]
  subnet                      = module.vpc.public_subnets[0]
  security_groups             = [module.vpc.default_security_group_id]
  associate_public_ip_address = var.ec2["associate_public_ip_address"]
  name                        = var.globals["shortname"]
  namespace                   = var.globals["namespace"]
  stage                       = var.globals["stage"]
  instance_profile            = aws_iam_instance_profile.ec2_instance.name
  user_data                   = <<-EOF
    #!/bin/bash
    sudo yum update -y
    sudo amazon-linux-extras install docker -y
    sudo service docker start
    sudo usermod -a -G docker ec2-user
    sudo curl -L https://github.com/docker/compose/releases/download/1.25.4/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    mkdir /home/ec2-user/app
  EOF
  security_group_rules = [
    {
      type        = "egress"
      from_port   = 0
      to_port     = 65535
      protocol    = "-1"
      cidr_blocks = ["0.0.0.0/0"]
    },
    {
      type        = "ingress"
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    },
    {
      type        = "ingress"
      from_port   = 80
      to_port     = 80
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    },
    {
      type        = "ingress"
      from_port   = 443
      to_port     = 443
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    }
  ]
}

# ECR
module "ecr" {
  source               = "cloudposse/ecr/aws"
  version              = "0.35.0"
  name                 = var.globals["shortname"]
  namespace            = var.globals["namespace"]
  stage                = var.globals["stage"]
  image_tag_mutability = "MUTABLE"
}