output "gke_node_service_account_email" {
  value = local.gke_node_service_account_email
}

output "jenkins_service_account_email" {
  value = google_service_account.jenkins_deployer.email
}

output "jenkins_service_account_name" {
  value = google_service_account.jenkins_deployer.name
}