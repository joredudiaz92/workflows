pipeline {
    agent any

    environment {
        REGISTRY_USER = 'joredudiaz92'
        IMAGE_NAME    = 'workflows'
        IMAGE_TAG     = "${BUILD_NUMBER}"
        IMAGE_FULL    = "${REGISTRY_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
    }

    tools {
        jdk 'Java25'
    }

    stages {
        stage('Build Java App') {
            steps {
                echo 'Building Java App...'
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x checkstyleMain -x checkstyleTest -x test'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    echo 'Building Docker image...'
                    dockerImage = docker.build("${IMAGE_FULL}")
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://docker.io', 'docker-hub-credentials') {
                        dockerImage.push()
                        dockerImage.push('latest') // Optional: Also tag and push as latest
                    }
                }
            }
        }
    }

    post {
        always {
            sh "docker rmi ${IMAGE_FULL} || true"
            sh "docker rmi ${REGISTRY_USER}/${IMAGE_NAME}:latest || true"
        }
    }
}
