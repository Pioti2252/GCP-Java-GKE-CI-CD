resource "google_sql_database_instance" "this" {
  name             = var.instance_name
  region           = var.region
  database_version = var.database_version

  deletion_protection = var.deletion_protection

  settings {
    tier              = var.tier
    availability_type = var.availability_type
    disk_type         = var.disk_type
    disk_size         = var.disk_size

    backup_configuration {
      enabled                        = var.backup_enabled
      point_in_time_recovery_enabled = var.point_in_time_recovery_enabled
    }

    ip_configuration {
      ipv4_enabled    = false
      private_network = var.private_network
    }

    user_labels = {
      project     = var.project_name
      environment = var.environment
      managed_by  = "terraform"
    }
  }
}

resource "google_sql_database" "database" {
  name     = var.database_name
  instance = google_sql_database_instance.this.name
}

resource "google_sql_user" "user" {
  name     = var.database_user
  instance = google_sql_database_instance.this.name
  password = var.database_password
}