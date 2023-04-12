provider "aws" {
  region = var.region

  default_tags {
    tags = {
      Terraform = "true"
    }
  }
}

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 3.0.0"
    }
  }
}
