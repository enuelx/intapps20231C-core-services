variable "region" {
  description = "Region AWS Account - sa-east-1 for default"
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

# variable "s3_intapps" {
#   description = "Map for bucket"
# }

variable "ec2" {
  description = "Map for webapp ec2 instance"
}
