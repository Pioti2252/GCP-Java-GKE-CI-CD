output "artifact_registry_repository_id" {
  value = module.artifact_registry.repository_id
}

output "artifact_registry_repository_name" {
  value = module.artifact_registry.repository_name
}

output "gke_cluster_name" {
  value = module.gke.cluster_name
}

output "gke_cluster_location" {
  value = module.gke.cluster_location
}

output "gke_cluster_endpoint" {
  value     = module.gke.cluster_endpoint
  sensitive = true
}

output "cloud_sql_instance_name" {
  value = module.cloud_sql.instance_name
}

output "cloud_sql_connection_name" {
  value = module.cloud_sql.connection_name
}

output "cloud_sql_public_ip_address" {
  value = module.cloud_sql.public_ip_address
}

output "cloud_sql_database_name" {
  value = module.cloud_sql.database_name
}

output "gke_node_service_account_email" {
  value = module.iam.gke_node_service_account_email
}

output "jenkins_service_account_email" {
  value = module.iam.jenkins_service_account_email
}

output "jenkins_vm_external_ip" {
  value = module.jenkins_vm.jenkins_vm_external_ip
}

output "jenkins_url" {
  value = module.jenkins_vm.jenkins_url
}

output "jenkins_vm_service_account_email" {
  value = module.jenkins_vm.jenkins_vm_service_account_email
}