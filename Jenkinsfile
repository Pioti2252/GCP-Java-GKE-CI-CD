pipeline {
    agent {
    label 'jenkins-gcp'
    }

    environment {
        PROJECT_ID = 'gcp-java-gke-ci-cd'
        REGION = 'europe-central2'

        DEV_CLUSTER = 'java-shop-dev-gke'
        DEV_REPOSITORY = 'java-shop-dev'

        PROD_CLUSTER = 'java-shop-prod-gke'
        PROD_REPOSITORY = 'java-shop-prod'

        IMAGE_NAME = 'java-shop'

        IMAGE_TAG = "dev-${env.BUILD_NUMBER}"
        IMAGE_URI = "${REGION}-docker.pkg.dev/${PROJECT_ID}/${DEV_REPOSITORY}/${IMAGE_NAME}:${IMAGE_TAG}"

        PROD_IMAGE_TAG = "prod-${env.BUILD_NUMBER}"
        PROD_IMAGE_URI = "${REGION}-docker.pkg.dev/${PROJECT_ID}/${PROD_REPOSITORY}/${IMAGE_NAME}:${PROD_IMAGE_TAG}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Run tests') {
            steps {
                dir('java-shop') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean test'
                }
            }
        }
        stage('IaC security scan') {
            steps {
                sh '''
                    echo "Scanning Terraform and Kubernetes manifests with Checkov..."

                    docker run --rm \
                    -v "$PWD:/src" \
                    bridgecrew/checkov:latest \
                    -d /src \
                    --quiet \
                    --soft-fail
                '''
            }
        }

        stage('Build Docker image') {
            steps {
                dir('java-shop') {
                    sh 'docker build -t $IMAGE_URI .'
                }
            }
        }
        stage('Scan Docker image') {
            steps {
                sh '''
                    echo "Scanning Docker image with Trivy..."

                    docker run --rm \
                    -v /var/run/docker.sock:/var/run/docker.sock \
                    aquasec/trivy:latest image \
                    --severity HIGH,CRITICAL \
                    --exit-code 0 \
                    --no-progress \
                    $IMAGE_URI
                '''
            }
        }

        stage('Configure Docker auth') {
            steps {
                sh 'gcloud auth configure-docker $REGION-docker.pkg.dev --quiet'
            }
        }

        stage('Push Docker image') {
            steps {
                sh 'docker push $IMAGE_URI'
            }
        }

        stage('Deploy to DEV') {
            steps {
                script {
                    try {
                        sh '''
                            gcloud container clusters get-credentials $DEV_CLUSTER \
                            --region $REGION \
                            --project $PROJECT_ID

                            kubectl apply -k k8s/overlays/dev

                            kubectl set image deployment/java-shop-app \
                            java-shop-app=$IMAGE_URI \
                            -n java-shop

                            kubectl rollout status deployment/java-shop-app -n java-shop
                        '''
                    } catch (err) {
                        sh '''
                            echo "DEV deployment failed. Rolling back..."

                            kubectl rollout undo deployment/java-shop-app -n java-shop || true
                            kubectl rollout status deployment/java-shop-app -n java-shop || true

                            echo "DEV rollback attempted."
                        '''
                        throw err
                    }
                }
            }
        }

        stage('Smoke test DEV') {
            steps {
                sh '''
                    echo "Running DEV smoke tests..."

                    DEV_INGRESS_IP=$(kubectl get ingress java-shop-ingress -n java-shop -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

                    if [ -z "$DEV_INGRESS_IP" ]; then
                    echo "DEV Ingress IP is empty"
                    kubectl get ingress -n java-shop
                    exit 1
                    fi

                    DEV_APP_URL="http://${DEV_INGRESS_IP}"

                    echo "DEV app URL: $DEV_APP_URL"

                    for i in {1..18}; do
                        echo "DEV health check attempt $i/18"

                        if curl -fsS "$DEV_APP_URL/actuator/health"; then
                            echo "DEV health endpoint is OK"
                            break
                        fi

                        echo "DEV health endpoint is not ready yet. Waiting 10 seconds..."
                        sleep 10

                        if [ "$i" -eq 18 ]; then
                            echo "DEV health check failed after all retries"
                            exit 1
                        fi
                    done

                    echo "Checking DEV products endpoint..."

                    for i in {1..12}; do
                        echo "DEV products check attempt $i/12"

                        if curl -fsS "$DEV_APP_URL/api/products"; then
                            echo "DEV products endpoint is OK"
                            break
                        fi

                        echo "DEV products endpoint is not ready yet. Waiting 10 seconds..."
                        sleep 10

                        if [ "$i" -eq 12 ]; then
                            echo "DEV products check failed after all retries"
                            exit 1
                        fi
                    done
                '''
            }
        }

        stage('Show DEV resources') {
            steps {
                sh 'kubectl get pods -n java-shop'
                sh 'kubectl get svc -n java-shop'
                sh 'kubectl get ingress -n java-shop'
                sh 'kubectl get hpa -n java-shop'
            }
        }

                stage('Approve PROD deployment') {
            steps {
                input message: 'Deploy this build to PROD?', ok: 'Deploy to PROD'
            }
        }

        stage('Deploy to PROD') {
            steps {
                script {
                    try {
                        sh 'docker tag $IMAGE_URI $PROD_IMAGE_URI'
                        sh 'docker push $PROD_IMAGE_URI'

                        sh '''
                            gcloud container clusters get-credentials $PROD_CLUSTER \
                            --region $REGION \
                            --project $PROJECT_ID

                            kubectl apply -k k8s/overlays/prod

                            kubectl set image deployment/java-shop-app \
                            java-shop-app=$PROD_IMAGE_URI \
                            -n java-shop

                            kubectl rollout status deployment/java-shop-app -n java-shop
                        '''
                    } catch (err) {
                        sh '''
                            echo "PROD deployment failed. Rolling back..."

                            kubectl rollout undo deployment/java-shop-app -n java-shop || true
                            kubectl rollout status deployment/java-shop-app -n java-shop || true

                            echo "PROD rollback attempted."
                        '''
                        throw err
                    }
                }
            }
        }

        stage('Smoke test PROD') {
            steps {
                sh '''
                    echo "Running PROD smoke tests..."

                    PROD_INGRESS_IP=$(kubectl get ingress java-shop-ingress -n java-shop -o jsonpath='{.status.loadBalancer.ingress[0].ip}')

                    if [ -z "$PROD_INGRESS_IP" ]; then
                    echo "PROD Ingress IP is empty"
                    kubectl get ingress -n java-shop
                    exit 1
                    fi

                    PROD_APP_URL="http://${PROD_INGRESS_IP}"

                    echo "PROD app URL: $PROD_APP_URL"

                    for i in {1..18}; do
                        echo "PROD health check attempt $i/18"

                        if curl -fsS "$PROD_APP_URL/actuator/health"; then
                            echo "PROD health endpoint is OK"
                            break
                        fi

                        echo "PROD health endpoint is not ready yet. Waiting 10 seconds..."
                        sleep 10

                        if [ "$i" -eq 18 ]; then
                            echo "PROD health check failed after all retries"
                            exit 1
                        fi
                    done

                    echo "Checking PROD products endpoint..."

                    for i in {1..12}; do
                        echo "PROD products check attempt $i/12"

                        if curl -fsS "$PROD_APP_URL/api/products"; then
                            echo "PROD products endpoint is OK"
                            break
                        fi

                        echo "PROD products endpoint is not ready yet. Waiting 10 seconds..."
                        sleep 10

                        if [ "$i" -eq 12 ]; then
                            echo "PROD products check failed after all retries"
                            exit 1
                        fi
                    done
                '''
            }
        }

        stage('Show PROD resourcesss') {
            steps {
                sh 'kubectl get pods -n java-shop'
                sh 'kubectl get svc -n java-shop'
                sh 'kubectl get ingress -n java-shop'
                sh 'kubectl get hpa -n java-shop'
            }
        }
    }
    post {
        failure {
            sh '''
                echo "Pipeline failed. Showing basic Kubernetes diagnostics..."

                echo "Current context:"
                kubectl config current-context || true

                echo "Pods:"
                kubectl get pods -n java-shop || true

                echo "Deployment:"
                kubectl describe deployment java-shop-app -n java-shop || true

                echo "Recent events:"
                kubectl get events -n java-shop --sort-by=.metadata.creationTimestamp | tail -30 || true
            '''
        }

        success {
            echo "Pipeline finished successfully. Application deployed and smoke tests passed."
        }
    }
}