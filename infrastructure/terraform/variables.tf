variable "region" {
  description = "Region AWS Account - us-east-1 for default"
  type        = string
}

variable "globals" {
  description = "Map for global settings"
}

variable "vpc" {
  description = "Map for aws virtual private cloud"
}

variable "mq_broker" {
  description = "Map for RabbitMQ on AWS MQ"
}

variable "s3" {
  description = "Map for bucket"
}

variable "ec2" {
  description = "Map for webapp ec2 instance"
}
