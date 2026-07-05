terraform {
  backend "gcs" {
    bucket = "gcp-java-gke-ci-cd-terraform-state"
    prefix = "terraform/prod"
  }
}