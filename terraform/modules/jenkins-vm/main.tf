resource "google_service_account" "jenkins_vm" {
  account_id   = "jenkins-vm-sa"
  display_name = "Jenkins VM Service Account"
  description  = "Service account used by Jenkins VM"
}

resource "google_project_iam_member" "jenkins_vm_artifact_registry_writer" {
  project = var.project_id
  role    = "roles/artifactregistry.writer"
  member  = "serviceAccount:${google_service_account.jenkins_vm.email}"
}

resource "google_project_iam_member" "jenkins_vm_container_developer" {
  project = var.project_id
  role    = "roles/container.developer"
  member  = "serviceAccount:${google_service_account.jenkins_vm.email}"
}

resource "google_project_iam_member" "jenkins_vm_service_account_user" {
  project = var.project_id
  role    = "roles/iam.serviceAccountUser"
  member  = "serviceAccount:${google_service_account.jenkins_vm.email}"
}

resource "google_compute_firewall" "jenkins_ui" {
  name    = "allow-jenkins-ui"
  network = var.network

  allow {
    protocol = "tcp"
    ports    = ["8080"]
  }

  source_ranges = [var.allowed_jenkins_cidr]
  target_tags   = ["jenkins-vm"]
}

resource "google_compute_firewall" "jenkins_ssh" {
  name    = "allow-jenkins-ssh"
  network = var.network

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }

  source_ranges = [var.allowed_ssh_cidr]
  target_tags   = ["jenkins-vm"]
}

resource "google_compute_instance" "jenkins" {
  name         = "jenkins-${var.environment}"
  zone         = var.zone
  machine_type = var.machine_type

  tags = ["jenkins-vm"]

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2204-lts"
      size  = var.disk_size_gb
      type  = "pd-balanced"
    }
  }

  network_interface {
    network = var.network

    access_config {}
  }

  service_account {
    email = google_service_account.jenkins_vm.email
    scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]
  }

  metadata_startup_script = file("${path.module}/startup-script.sh")

  labels = {
    project     = "java-shop"
    environment = var.environment
    managed_by  = "terraform"
  }
  lifecycle {
    prevent_destroy = true

    ignore_changes = [
      metadata,
      metadata_startup_script,
      tags,
    ]
  }
}