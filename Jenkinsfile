
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t payment-service:${BUILD_NUMBER} .'
            }
        }
    }

    post {
        success {
            echo 'Payment Service pipeline completed successfully.'
        }
        failure {
            echo 'Payment Service pipeline failed. Check the stage logs.'
        }
    }
}
