resource "google_container_cluster" "this" {
  name     = var.cluster_name
  location = var.region

  enable_autopilot = true

  deletion_protection = var.deletion_protection

  release_channel {
    channel = var.release_channel
  }

  network    = var.network
  subnetwork = var.subnetwork

  ip_allocation_policy {}

  workload_identity_config {
    workload_pool = "${var.project_id}.svc.id.goog"
  }

  addons_config {
    http_load_balancing {
      disabled = false
    }
  }

resource_labels = {
  project     = var.project_name
  environment = var.environment
  managed_by  = "terraform"
}
}