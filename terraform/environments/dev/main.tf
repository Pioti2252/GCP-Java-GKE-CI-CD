module "artifact_registry" {
  source = "../../modules/artifact-registry"

  region        = var.region
  repository_id = "java-shop-dev"
  environment   = var.environment
<<<<<<< HEAD
}

module "gke" {
  source = "../../modules/gke"

  project_id          = var.project_id
  cluster_name        = "java-shop-dev-gke"
  region              = var.region
  environment         = var.environment
  deletion_protection = false
}

module "cloud_sql" {
  source = "../../modules/cloud-sql"

  instance_name       = "java-shop-dev-db"
  region              = var.region
  environment         = var.environment
  tier                = "db-f1-micro"
  disk_size           = 10
  backup_enabled      = false
  deletion_protection = false

  database_name     = "java_shop"
  database_user     = "java_shop_user"
  database_password = var.database_password
=======
>>>>>>> 4d6c52a03260a3e928b734a6ed2bea6d53d6a093
}