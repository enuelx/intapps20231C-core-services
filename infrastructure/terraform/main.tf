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
  enable_nat_gateway     = var.vpc["enable_nat_gateway"]
  single_nat_gateway     = var.vpc["single_nat_gateway"]
  one_nat_gateway_per_az = var.vpc["one_nat_gateway_per_az"]
  enable_vpn_gateway     = var.vpc["enable_vpn_gateway"]
  enable_dns_hostnames   = var.vpc["enable_dns_hostnames"]
}

# # MQ
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
    },
    {
      "Action": [
        "logs:*"
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

module "ec2_instance_app" {
  source                      = "cloudposse/ec2-instance/aws"
  version                     = "0.47.1"
  ami                         = data.aws_ami.amazon_linux_2.id
  vpc_id                      = module.vpc.vpc_id
  ssh_key_pair                = var.ec2["ssh_key_pair"]
  subnet                      = module.vpc.public_subnets[0]
  security_groups             = [module.vpc.default_security_group_id]
  associate_public_ip_address = var.ec2["associate_public_ip_address"]
  name                        = "${var.globals["shortname"]}-app"
  namespace                   = var.globals["namespace"]
  stage                       = var.globals["stage"]
  instance_profile            = aws_iam_instance_profile.ec2_instance.name
  user_data                   = file("../templates/app_installation.sh")
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

# Grafana
resource "aws_iam_role" "ec2_instance_grafana" {
  name               = "grafana_iam_role"
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

resource "aws_iam_role_policy" "ec2_instance_grafana" {
  name   = "${var.globals["namespace"]}-${var.globals["stage"]}-ec2-grafana"
  role   = aws_iam_role.ec2_instance_grafana.id
  policy = <<EOF
{
   "Version": "2012-10-17",
   "Statement": [
       {
           "Sid": "AllowCloudWatch",
           "Effect": "Allow",
           "Action": [
               "cloudwatch:*",
               "logs:*"
           ],
           "Resource": "*"
       },
       {
           "Sid": "AllowEC2",
           "Effect": "Allow",
           "Action": [
               "ec2:*"
           ],
           "Resource": "*"
       },
       {
          "Sid": "AllowReadingECR",
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

resource "aws_iam_instance_profile" "ec2_instance_grafana" {
  name = "${var.globals["namespace"]}-${var.globals["stage"]}-ec2-grafana"
  role = aws_iam_role.ec2_instance_grafana.name
}

module "ec2_instance_grafana" {
  source                      = "cloudposse/ec2-instance/aws"
  version                     = "0.47.1"
  ami                         = data.aws_ami.amazon_linux_2.id
  vpc_id                      = module.vpc.vpc_id
  ssh_key_pair                = var.ec2["ssh_key_pair"]
  subnet                      = module.vpc.public_subnets[0]
  security_groups             = [module.vpc.default_security_group_id]
  associate_public_ip_address = var.ec2["associate_public_ip_address"]
  name                        = "${var.globals["shortname"]}-grafana"
  namespace                   = var.globals["namespace"]
  stage                       = var.globals["stage"]
  instance_profile            = aws_iam_instance_profile.ec2_instance_grafana.name
  user_data                   = file("../templates/grafana_installation.sh")
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
      from_port   = 3000
      to_port     = 3000
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    },
    {
      type        = "ingress"
      from_port   = 443
      to_port     = 443
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    },
    {
      type        = "ingress"
      from_port   = 80
      to_port     = 80
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    }
  ]
}

resource "aws_security_group" "alb_sg_grafana" {
  name        = "alb-sg"
  description = "Allow inbound traffic to ALB"
  vpc_id      = module.vpc.vpc_id

  egress {
    from_port   = 0
    to_port     = 65535
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_acm_certificate" "grafana_cert" {
  domain_name       = "grafana.deliver.ar"
  validation_method = "DNS"
}

resource "aws_lb" "grafana_lb" {
  name               = "${var.globals["shortname"]}-grafana-lb"
  internal           = false
  load_balancer_type = "application"

  subnets         = module.vpc.public_subnets
  security_groups = [aws_security_group.alb_sg_grafana.id]

  tags = {
    Name = "${var.globals["shortname"]}-grafana-lb"
  }
}

resource "aws_lb_target_group" "grafana_target_group" {
  port     = 3000
  protocol = "HTTP"
  vpc_id   = module.vpc.vpc_id
  health_check {
    path = "/"
  }
}

resource "aws_lb_listener" "grafana_listener_http" {
  load_balancer_arn = aws_lb.grafana_lb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "redirect"
    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource "aws_lb_listener" "grafana_listener_https" {
  load_balancer_arn = aws_lb.grafana_lb.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = aws_acm_certificate.grafana_cert.arn

  default_action {
    target_group_arn = aws_lb_target_group.grafana_target_group.arn
    type             = "forward"
  }
}

resource "aws_lb_target_group_attachment" "grafana_target_group_attachment" {
  target_group_arn = aws_lb_target_group.grafana_target_group.arn
  target_id        = module.ec2_instance_grafana.id
}
