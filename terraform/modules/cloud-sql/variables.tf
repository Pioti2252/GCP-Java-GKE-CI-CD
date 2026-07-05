variable "instance_name" {
  description = "Cloud SQL instance name"
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

variable "project_name" {
  description = "Project name label"
  type        = string
  default     = "java-shop"
}

variable "database_version" {
  description = "PostgreSQL database version"
  type        = string
  default     = "POSTGRES_15"
}

variable "tier" {
  description = "Cloud SQL machine tier"
  type        = string
}

variable "availability_type" {
  description = "Cloud SQL availability type"
  type        = string
  default     = "ZONAL"
}

variable "disk_type" {
  description = "Cloud SQL disk type"
  type        = string
  default     = "PD_SSD"
}

variable "disk_size" {
  description = "Cloud SQL disk size in GB"
  type        = number
  default     = 10
}

variable "backup_enabled" {
  description = "Enable backups"
  type        = bool
  default     = false
}

variable "point_in_time_recovery_enabled" {
  description = "Enable PITR"
  type        = bool
  default     = false
}

variable "deletion_protection" {
  description = "Enable deletion protection"
  type        = bool
  default     = false
}

variable "database_name" {
  description = "Application database name"
  type        = string
  default     = "java_shop"
}

variable "database_user" {
  description = "Application database user"
  type        = string
  default     = "java_shop_user"
}

variable "database_password" {
  description = "Application database password"
  type        = string
  sensitive   = true
}