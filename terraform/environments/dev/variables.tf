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

variable "allowed_ssh_cidr" {
  description = "CIDR allowed to access Jenkins VM over SSH"
  type        = string

  validation {
    condition     = var.allowed_ssh_cidr != "0.0.0.0/0"
    error_message = "SSH access must not be open to 0.0.0.0/0."
  }
}

variable "allowed_jenkins_cidr" {
  description = "CIDR allowed to access Jenkins UI"
  type        = string

  validation {
    condition     = var.allowed_jenkins_cidr != "0.0.0.0/0"
    error_message = "Jenkins UI access must not be open to 0.0.0.0/0."
  }
}