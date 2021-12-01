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