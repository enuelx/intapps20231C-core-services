region = "sa-east-1"

globals = {
  namespace = "core"
  stage     = "prod"
  shortname = "intapps"
}

vpc = {
  cidr                   = "10.190.0.0/16"
  azs                    = ["sa-east-1a", "sa-east-1b"]
  private_subnets        = ["10.190.100.0/24", "10.190.101.0/24"]
  public_subnets         = ["10.190.200.0/24", "10.190.201.0/24"]
  enable_nat_gateway     = true
  single_nat_gateway     = true
  one_nat_gateway_per_az = false
  enable_vpn_gateway     = true
  enable_dns_hostnames   = true
}

mq_broker = {
  apply_immediately            = true
  auto_minor_version_upgrade   = true
  deployment_mode              = "SINGLE_INSTANCE"
  engine_type                  = "RabbitMQ"
  engine_version               = "3.10.10"
  host_instance_type           = "mq.t3.micro"
  publicly_accessible          = false
  general_log_enabled          = true
  audit_log_enabled            = false
  encryption_enabled           = true
  use_aws_owned_key            = true
  allowed_ingress_ports        = [8162, 5671]
  use_existing_security_groups = false
}

ec2 = {
  ssh_key_pair                = "intapps-2023"
  associate_public_ip_address = true
  ebs_volume_size             = 30
  ebs_volume_count            = 1
}
