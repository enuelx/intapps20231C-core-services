region = "sa-east-1"

globals = {
  namespace = "core"
  stage     = "prod"
  shortname = "intapps"
}

vpc = {
  cidr                   = "10.190.0.0/16"
  azs                    = ["us-east-1a", "us-east-1b", "us-east-1c"]
  private_subnets        = ["10.190.100.0/24", "10.190.101.0/24", "10.190.102.0/24"]
  public_subnets         = ["10.190.200.0/24", "10.190.201.0/24", "10.190.202.0/24"]
  database_subnets       = ["10.190.21.0/24", "10.190.22.0/24"]
  enable_nat_gateway     = true
  single_nat_gateway     = true
  one_nat_gateway_per_az = false
  enable_vpn_gateway     = true
  enable_dns_hostnames   = true
}

mq_broker = {
  apply_immediately          = true
  auto_minor_version_upgrade = true
  deployment_mode            = "SINGLE_INSTANCE"
  engine_type                = "RabbitMQ"
  engine_version             = "3.10.10"
  host_instance_type         = "mq.t3.micro"
  publicly_accessible        = false
  general_log_enabled        = false
  audit_log_enabled          = false
  encryption_enabled         = true
  use_aws_owned_key          = true
  allowed_ingress_ports      = [8162, 5671]
}

s3_store_jar = {
  enabled            = true
  user_enabled       = false
  versioning_enabled = true
  acl                = "private"
  bucket_key_enabled = true
  sse_algorithm      = "aws:kms"
}

ec2 = {
  ssh_key_pair = "ubuntu"
}