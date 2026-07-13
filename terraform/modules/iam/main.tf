locals {
  gke_node_service_account_email = "${var.project_number}-compute@developer.gserviceaccount.com"
}

resource "google_project_iam_member" "gke_artifact_registry_reader" {
  project = var.project_id
  role    = "roles/artifactregistry.reader"
  member  = "serviceAccount:${local.gke_node_service_account_email}"
}

resource "google_project_iam_member" "jenkins_artifact_registry_writer" {
  project = var.project_id
  role    = "roles/artifactregistry.writer"
  member  = "serviceAccount:${google_service_account.jenkins_deployer.email}"
}

resource "google_project_iam_member" "jenkins_container_developer" {
  project = var.project_id
  role    = "roles/container.developer"
  member  = "serviceAccount:${google_service_account.jenkins_deployer.email}"
}

resource "google_project_iam_member" "jenkins_service_account_user" {
  project = var.project_id
  role    = "roles/iam.serviceAccountUser"
  member  = "serviceAccount:${google_service_account.jenkins_deployer.email}"
}