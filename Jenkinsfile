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

        DEV_APP_URL = 'http://8.232.215.207/'
        PROD_APP_URL = 'http://34.36.34.100/'
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
                    kubectl set image deployment/java-shop-app java-shop-app=$IMAGE_URI -n java-shop
                    kubectl rollout status deployment/java-shop-app -n java-shop
                '''
            }
        }

        stage('Smoke test DEV') {
            steps {
                sh '''
                    echo "Waiting before DEV smoke tests..."
                    sleep 20

                    echo "Checking DEV health endpoint..."
                    curl -f $DEV_APP_URL/actuator/health

                    echo "Checking DEV products endpoint..."
                    curl -f $DEV_APP_URL/api/products
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
                    kubectl set image deployment/java-shop-app java-shop-app=$PROD_IMAGE_URI -n java-shop
                    kubectl rollout status deployment/java-shop-app -n java-shop
                '''
            }
        }

        stage('Smoke test PROD') {
            steps {
                sh '''
                    echo "Waiting before PROD smoke tests..."
                    sleep 20

                    echo "Checking PROD health endpoint..."
                    curl -f $PROD_APP_URL/actuator/health

                    echo "Checking PROD products endpoint..."
                    curl -f $PROD_APP_URL/api/products
                '''
            }
        }

        stage('Show PROD resources') {
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