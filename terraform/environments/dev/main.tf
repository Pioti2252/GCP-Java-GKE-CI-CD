module "artifact_registry" {
  source = "../../modules/artifact-registry"

  region        = var.region
  repository_id = "java-shop-dev"
  environment   = var.environment
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
}

module "iam" {
  source = "../../modules/iam"

  project_id         = var.project_id
  project_number     = var.project_number
  environment        = var.environment

  jenkins_service_account_id           = "jenkins-dev-deployer"
  jenkins_service_account_display_name = "Jenkins DEV Deployer"
}

module "jenkins_vm" {
  source = "../../modules/jenkins-vm"

  project_id  = var.project_id
  zone        = var.zone
  environment = var.environment

  machine_type = "e2-medium"
  disk_size_gb = 30

  allowed_ssh_cidr     = "0.0.0.0/0"
  allowed_jenkins_cidr = "0.0.0.0/0"
}