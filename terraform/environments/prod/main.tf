module "artifact_registry" {
  source = "../../modules/artifact-registry"

  region        = var.region
  repository_id = "java-shop-prod"
  environment   = var.environment
}

module "gke" {
  source = "../../modules/gke"

  project_id          = var.project_id
  cluster_name        = "java-shop-prod-gke"
  region              = var.region
  environment         = var.environment
  deletion_protection = true
}

module "cloud_sql" {
  source = "../../modules/cloud-sql"

  instance_name       = "java-shop-prod-db"
  region              = var.region
  environment         = var.environment
  tier                = "db-f1-micro"
  disk_size           = 10
  backup_enabled      = true
  deletion_protection = true

  database_name     = "java_shop"
  database_user     = "java_shop_user"
  database_password = var.database_password
}

module "iam" {
  source = "../../modules/iam"

  project_id     = var.project_id
  project_number = var.project_number
  environment    = var.environment

  jenkins_service_account_id           = "jenkins-prod-deployer"
  jenkins_service_account_display_name = "Jenkins PROD Deployer"
}