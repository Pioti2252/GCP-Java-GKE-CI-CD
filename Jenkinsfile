pipeline {
    agent any

    environment {
        PROJECT_ID = 'gcp-java-gke-ci-cd'
        REGION = 'europe-central2'

        DEV_CLUSTER = 'java-shop-dev-gke'
        DEV_REPOSITORY = 'java-shop-dev'

        IMAGE_NAME = 'java-shop'
        IMAGE_TAG = "dev-${env.BUILD_NUMBER}"
        IMAGE_URI = "${REGION}-docker.pkg.dev/${PROJECT_ID}/${DEV_REPOSITORY}/${IMAGE_NAME}:${IMAGE_TAG}"

        DEV_APP_URL = 'http:/8.232.215.207'
        PROD_APP_URL = 'http:/34.36.34.100'
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

        stage('Build Docker image') {
            steps {
                dir('java-shop') {
                    sh 'docker build -t $IMAGE_URI .'
                }
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
                sh '''
                    gcloud container clusters get-credentials $DEV_CLUSTER --region $REGION --project $PROJECT_ID
                    kubectl apply -k k8s/overlays/dev
                    kubectl set image deployment/java-shop-app java-shop-app=$IMAGE_URI -n java-shop
                    kubectl rollout status deployment/java-shop-app -n java-shop
                '''
            }
        }

        stage('Smoke test DEV') {
            steps {
                sh '''
                    echo "Running DEV smoke tests..."

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
            environment {
                PROD_CLUSTER = 'java-shop-prod-gke'
                PROD_REPOSITORY = 'java-shop-prod'
                PROD_IMAGE_TAG = "prod-${env.BUILD_NUMBER}"
                PROD_IMAGE_URI = "${REGION}-docker.pkg.dev/${PROJECT_ID}/${PROD_REPOSITORY}/${IMAGE_NAME}:${PROD_IMAGE_TAG}"
            }
            steps {
                sh 'docker tag $IMAGE_URI $PROD_IMAGE_URI'
                sh 'docker push $PROD_IMAGE_URI'

                sh '''
                    gcloud container clusters get-credentials $PROD_CLUSTER  --region $REGION --project $PROJECT_ID
                    kubectl apply -k k8s/overlays/prod
                    kubectl set image deployment/java-shop-app java-shop-app=$PROD_IMAGE_URI -n java-shop
                    kubectl rollout status deployment/java-shop-app -n java-shop
                '''
            }
        }

        stage('Smoke test PROD') {
            steps {
                sh '''
                    echo "Running PROD smoke tests...."

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