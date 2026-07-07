#!/bin/bash
set -e

echo "=== Updating packages ==="
apt-get update -y
apt-get install -y apt-transport-https ca-certificates curl gnupg wget git software-properties-common

echo "=== Installing Java 21 ==="
apt-get install -y openjdk-21-jdk

echo "=== Installing Jenkins ==="
mkdir -p /etc/apt/keyrings

wget -O /etc/apt/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key

echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | \
  tee /etc/apt/sources.list.d/jenkins.list > /dev/null

apt-get update -y
apt-get install -y jenkins

systemctl enable jenkins
systemctl start jenkins

echo "=== Installing Docker ==="
apt-get install -y docker.io

systemctl enable docker
systemctl start docker

usermod -aG docker jenkins

echo "=== Installing Google Cloud CLI repository ==="
mkdir -p /usr/share/keyrings

curl -fsSL https://packages.cloud.google.com/apt/doc/apt-key.gpg | \
  gpg --dearmor -o /usr/share/keyrings/cloud.google.gpg

echo "deb [signed-by=/usr/share/keyrings/cloud.google.gpg] https://packages.cloud.google.com/apt cloud-sdk main" | \
  tee /etc/apt/sources.list.d/google-cloud-sdk.list > /dev/null

apt-get update -y

echo "=== Installing GKE auth plugin ==="
apt-get install -y google-cloud-cli-gke-gcloud-auth-plugin || \
apt-get install -y google-cloud-sdk-gke-gcloud-auth-plugin

echo "=== Installing kubectl ==="
mkdir -p /etc/apt/keyrings

curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.35/deb/Release.key | \
  gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg

echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.35/deb/ /' | \
  tee /etc/apt/sources.list.d/kubernetes.list > /dev/null

apt-get update -y
apt-get install -y kubectl

echo "=== Configuring Docker auth for Jenkins user ==="
sudo -u jenkins gcloud auth configure-docker europe-central2-docker.pkg.dev --quiet || true

echo "=== Restarting services ==="
systemctl restart docker
systemctl restart jenkins

echo "=== Installed versions ==="
java -version || true
javac -version || true
docker --version || true
gcloud --version || true
kubectl version --client || true
gke-gcloud-auth-plugin --version || true

echo "=== Jenkins initial password path ==="
echo "/var/lib/jenkins/secrets/initialAdminPassword"