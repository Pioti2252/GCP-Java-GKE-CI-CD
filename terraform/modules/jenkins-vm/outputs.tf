output "jenkins_vm_name" {
  value = google_compute_instance.jenkins.name
}

output "jenkins_vm_external_ip" {
  value = google_compute_instance.jenkins.network_interface[0].access_config[0].nat_ip
}

output "jenkins_url" {
  value = "http://${google_compute_instance.jenkins.network_interface[0].access_config[0].nat_ip}:8080"
}

output "jenkins_vm_service_account_email" {
  value = google_service_account.jenkins_vm.email
}