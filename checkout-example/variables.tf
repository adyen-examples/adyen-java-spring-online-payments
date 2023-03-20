variable "api_dist" {
  description = "Location of the API distribution"
  type        = string
  default     = "build/libs/online-payments-0.0.1-SNAPSHOT"
}

variable "namespace" {
  description = "Namespace of the deployment"
  type        = string
  default     = "adyen-spring"
}

variable "environment" {
  description = "Environment of the deployment"
  type        = string
  default     = "development"
}

# ----
# AWS Settings

variable "aws_profile" {
    description = "AWS Profile tu use for deployment"
    type        = string
    default     = "default"
}

variable "aws_region" {
    description = "AWS Region to use for deployment"
    type        = string
    default     = "eu-west-1"
}

# -----
# Adyen credentials

variable "adyen_client_key" {
  type        = string
  description = "Client Key from our Adyen account for this application"
  sensitive = true
}

variable "adyen_api_key" {
  type        = string
  description = "API Key from our Adyen account for this application"
  sensitive = true
}

variable "adyen_hmac_key" {
  type        = string
  description = "HMAC Key from our Adyen account for this application"
  sensitive = true
}

variable "adyen_merchant_account" {
  type        = string
  description = "Adyen Merchant Account to use for this application"
}
