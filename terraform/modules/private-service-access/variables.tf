variable "project_id" {
  description = "GCP project ID"
  type        = string
}

variable "network_name" {
  description = "VPC network name"
  type        = string
  default     = "default"
}

variable "address_name" {
  description = "Name of reserved private IP range"
  type        = string
}

variable "prefix_length" {
  description = "Prefix length for private service access range"
  type        = number
  default     = 16
}