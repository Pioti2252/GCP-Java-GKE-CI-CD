variable "project_id" {
  description = "GCP project ID"
  type        = string
}

variable "region" {
  description = "GCP region"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "database_password" {
  description = "Database password for dev Cloud SQL"
  type        = string
  sensitive   = true
}

variable "project_number" {
  description = "GCP project number"
  type        = string
}

variable "zone" {
  description = "GCP zone"
  type        = string
}