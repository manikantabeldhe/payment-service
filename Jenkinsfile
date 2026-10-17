
pipeline {
    agent any

    environment {
        AWS_REGION = 'ap-south-1'
        AWS_ACCOUNT_ID = '558260071685'
        ECR_REPOSITORY = 'payment-service'
        IMAGE_URI = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t payment-service:${BUILD_NUMBER} .'
                sh 'docker tag payment-service:${BUILD_NUMBER} ${IMAGE_URI}:${BUILD_NUMBER}'
            }
        }

        stage('ECR Login') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'aws-ecr-credentials',
                    usernameVariable: 'AWS_ACCESS_KEY_ID',
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                )]) {
                    sh '''
                        set +x
                        export AWS_ACCESS_KEY_ID="$AWS_ACCESS_KEY_ID"
                        export AWS_SECRET_ACCESS_KEY="$AWS_SECRET_ACCESS_KEY"
                        export AWS_DEFAULT_REGION="$AWS_REGION"
                        aws ecr get-login-password --region "$AWS_REGION" \
                          | docker login --username AWS --password-stdin \
                            "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                    '''
                }
            }
        }

        stage('Push to ECR') {
            steps {
                sh 'docker push ${IMAGE_URI}:${BUILD_NUMBER}'
            }
        }
    }

    post {
        success {
            echo 'Payment Service image built and pushed to ECR successfully.'
        }
        failure {
            echo 'Payment Service pipeline failed. Check the stage logs.'
        }
    }
}
