module "artifact_registry" {
  source = "../../modules/artifact-registry"

  region        = var.region
  repository_id = "java-shop-dev"
  environment   = var.environment
}