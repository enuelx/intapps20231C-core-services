module "kms_key_lambda" {
  source                  = "cloudposse/kms-key/aws"
  version                 = "0.12.1"
  namespace               = var.globals["namespace"]
  stage                   = var.globals["stage"]
  name                    = "${var.globals["namespace"]}-${var.globals["stage"]}-lambdas"
  description             = "KMS key for lambdas"
  deletion_window_in_days = 30
  enable_key_rotation     = true
  alias                   = "alias/${var.globals["stage"]}/lambda"
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
  alias                   = "alias/${var.globals["stage"]}/ps"
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
  alias                   = "alias/${var.globals["stage"]}/s3"
}

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

module "mq_broker" {
  source                     = "cloudposse/mq-broker/aws"
  version                    = "2.0.1"
  namespace                  = var.globals["namespace"]
  stage                      = var.globals["stage"]
  name                       = "${var.globals["namespace"]}-${var.globals["stage"]}"
  apply_immediately          = var.mq_broker["apply_immediately"]
  auto_minor_version_upgrade = var.mq_broker["auto_minor_version_upgrade"]
  deployment_mode            = var.mq_broker["deployment_mode"]
  engine_type                = var.mq_broker["engine_type"]
  engine_version             = var.mq_broker["engine_version"]
  host_instance_type         = var.mq_broker["host_instance_type"]
  publicly_accessible        = var.mq_broker["publicly_accessible"]
  general_log_enabled        = var.mq_broker["general_log_enabled"]
  audit_log_enabled          = var.mq_broker["audit_log_enabled"]
  encryption_enabled         = var.mq_broker["encryption_enabled"]
  use_aws_owned_key          = var.mq_broker["use_aws_owned_key"]
  vpc_id                     = module.vpc.vpc_id
  subnet_ids                 = module.vpc.private_subnets
  security_groups            = module.vpc.default_security_group_id
}

module "s3_store_lambda_core" {
  source             = "cloudposse/s3-bucket/aws"
  version            = "3.0.0"
  acl                = var.s3_default_config["acl"]
  enabled            = var.s3_default_config["enabled"]
  user_enabled       = var.s3_default_config["user_enabled"]
  versioning_enabled = var.s3_default_config["versioning_enabled"]
  name               = "${var.globals["namespace"]}-${var.globals["stage"]}-store-lambda"
  stage              = var.globals["stage"]
  namespace          = var.globals["namespace"]
  bucket_key_enabled = var.s3_default_config["bucket_key_enabled"]
  kms_master_key_arn = module.kms_key_s3.key_arn
  sse_algorithm      = var.s3_default_config["sse_algorithm"]
}
