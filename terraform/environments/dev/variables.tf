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
<<<<<<< HEAD
}

variable "database_password" {
  description = "Database password for dev Cloud SQL"
  type        = string
  sensitive   = true
=======
>>>>>>> 4d6c52a03260a3e928b734a6ed2bea6d53d6a093
}