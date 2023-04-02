provider "aws" {
  region = var.region

  default_tags {
    tags = {
      Terraform   = "true"
      Environment = "prod"
      Vertical    = "core"
    }
  }
}

terraform {
  backend "s3" {
    encrypt        = true
    bucket         = "core-prod-tfstate"
    key            = "terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "ccore-prod-tfstate-lock"
  }
}
