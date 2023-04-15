terraform {
  backend "s3" {
    encrypt        = true
    bucket         = "tfstate-intapps"
    key            = "terraform.tfstate"
    region         = "sa-east-1"
    dynamodb_table = "tfstate-intapps-lock"
  }
}
