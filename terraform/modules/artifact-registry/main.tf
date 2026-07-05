resource "google_artifact_registry_repository" "docker_repo" {
  location      = var.region
  repository_id = var.repository_id
  description   = var.description
  format        = "DOCKER"

  labels = {
    project     = var.project_name
    environment = var.environment
    managed_by  = "terraform"
  }
}