variable "project_id" {
  description = "GCP project ID"
  type        = string
}

variable "zone" {
  description = "GCP zone for Jenkins VM"
  type        = string
}

variable "environment" {
  description = "Environment name"
  type        = string
}

variable "machine_type" {
  description = "Machine type for Jenkins VM"
  type        = string
  default     = "e2-medium"
}

variable "disk_size_gb" {
  description = "Boot disk size in GB"
  type        = number
  default     = 30
}

variable "network" {
  description = "VPC network"
  type        = string
  default     = "default"
}

variable "allowed_ssh_cidr" {
  description = "CIDR allowed to SSH into Jenkins VM"
  type        = string
  default     = "0.0.0.0/0"
}

variable "allowed_jenkins_cidr" {
  description = "CIDR allowed to access Jenkins UI"
  type        = string
  default     = "0.0.0.0/0"
}