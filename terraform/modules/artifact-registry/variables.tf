variable "region" {
  description = "GCP region"
  type        = string
}

variable "repository_id" {
  description = "Artifact Registry repository ID"
  type        = string
}

variable "description" {
  description = "Repository description"
  type        = string
  default     = "Docker repository for Java Shop application"
}

variable "project_name" {
  description = "Project name label"
  type        = string
  default     = "java-shop"
}

variable "environment" {
  description = "Environment name"
  type        = string
}