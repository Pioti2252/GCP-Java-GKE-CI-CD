resource "google_compute_global_address" "private_service_range" {
  name          = var.address_name
  purpose       = "VPC_PEERING"
  address_type  = "INTERNAL"
  prefix_length = var.prefix_length
  network       = "projects/${var.project_id}/global/networks/${var.network_name}"
}

resource "google_service_networking_connection" "private_vpc_connection" {
  network                 = "projects/${var.project_id}/global/networks/${var.network_name}"
  service                 = "servicenetworking.googleapis.com"
  reserved_peering_ranges = [google_compute_global_address.private_service_range.name]
}