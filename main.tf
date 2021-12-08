locals{
    domain = "${var.namespace}-${var.environment}-${substr(uuid(),0 , 8)}"
    domain_url = "http://${local.domain}.eu-west-1.elasticbeanstalk.com"
}

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.27"
    }
  }
  required_version = ">= 0.14.9"
}

provider "aws" {
  profile = var.aws_profile
  region  = var.aws_region
}

// Compressing our FAT Jar into a Zip file
data "archive_file" "api_dist_zip" {
  type        = "zip"
  source_file = "${path.root}/${var.api_dist}.jar"
  output_path = "${path.root}/${var.api_dist}.zip"
}

//Creating an AWS S3 Bucket
resource "aws_s3_bucket" "dist_bucket" {
  bucket = "${var.namespace}-elb-dist"
  acl    = "private"
}

// Pushing our ZIP file on the S3 bucket
resource "aws_s3_bucket_object" "dist_item" {
  key    = "${var.environment}/dist-${uuid()}"
  bucket = aws_s3_bucket.dist_bucket.id
  source = "${path.root}/${var.api_dist}.zip"
}

// Creating a new Beanstalk application
resource "aws_elastic_beanstalk_application" "adyen_test_application" {
  name        = "adyen-test-application"
  description = "An Adyen test checkout application running on AWS"
}

// Creating a new version for our application, with our ZIP file and a version number
resource "aws_elastic_beanstalk_application_version" "adyen_test_application_version" {
  application = aws_elastic_beanstalk_application.adyen_test_application.name
  bucket = aws_s3_bucket.dist_bucket.id
  key = aws_s3_bucket_object.dist_item.id
  name = "${var.namespace}-1.0.0"
}

// Creating a new Beanstalk environment with our application and version
resource "aws_elastic_beanstalk_environment" "adyen_test_application_environment" {
  name                = "adyen-test-application-environment"
  application         = aws_elastic_beanstalk_application.adyen_test_application.name
  solution_stack_name = "64bit Amazon Linux 2 v3.2.8 running Corretto 11"
  version_label = aws_elastic_beanstalk_application_version.adyen_test_application_version.name
  cname_prefix = local.domain

  setting {
    namespace = "aws:ec2:instances"
    name = "InstanceTypes"
    value = "t2.micro"
  }

  // Default Beanstalk role
  setting {
   namespace = "aws:autoscaling:launchconfiguration"
   name = "IamInstanceProfile"
   value = "aws-elasticbeanstalk-ec2-role"
  }

  // Sends logs to Cloudwatch
  setting {
    namespace = "aws:elasticbeanstalk:cloudwatch:logs"
    name = "StreamLogs"
    value = "true"
  }

  setting {
    name = "PORT"
    namespace = "aws:elasticbeanstalk:application:environment"
    value = "8080"
  }

  setting {
    name = "ADYEN_CLIENT_KEY"
    namespace = "aws:elasticbeanstalk:application:environment"
    value = var.adyen_client_key
  }

  setting {
    name = "ADYEN_API_KEY"
    namespace = "aws:elasticbeanstalk:application:environment"
    value = var.adyen_api_key
  }

  setting {
    name = "ADYEN_HMAC_KEY"
    namespace = "aws:elasticbeanstalk:application:environment"
    value = var.adyen_hmac_key
  }

  setting {
    name = "ADYEN_MERCHANT_ACCOUNT"
    namespace = "aws:elasticbeanstalk:application:environment"
    value = var.adyen_merchant_account
  }

  setting {
    name = "ADYEN_RETURN_URL"
    namespace = "aws:elasticbeanstalk:application:environment"
    value = local.domain_url
  }
}

output "demo_url" {
  value       = local.domain_url
  description = "The URL of the demo that is being hosted"
}

output "environment_url" {
  value       = "https://${var.aws_region}.console.aws.amazon.com/elasticbeanstalk/home?region=${var.aws_region}#/applications"
  description = "The URL to access the Beanstalk environments"
}

output "adyen_url" {
    value       = "https://ca-test.adyen.com/ca/ca/config/showthirdparty.shtml"
    description = "The URL to access the Webhooks part of the Adyen Customer Area"
}

