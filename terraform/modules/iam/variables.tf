variable "project_id" {
  description = "GCP project ID"
  type        = string
}

variable "project_number" {
  description = "GCP project number"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "jenkins_service_account_id" {
  description = "Jenkins deployer service account ID"
  type        = string
}

variable "jenkins_service_account_display_name" {
  description = "Jenkins deployer service account display name"
  type        = string
  default     = "Jenkins Deployer"
}