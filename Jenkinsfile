pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -B'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -B'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("finance-tracker:${env.BUILD_NUMBER}")
                }
            }
        }

        stage('Deploy to VPS') {
            steps {
                script {
                    // Копируем docker-compose.yml и конфиги на сервер
                    sh """
                        scp -o StrictHostKeyChecking=no docker-compose.yml ${DEPLOY_USER}@${DEPLOY_HOST}:/opt/finance-tracker/
                        scp -o StrictHostKeyChecking=no prometheus.yml ${DEPLOY_USER}@${DEPLOY_HOST}:/opt/finance-tracker/
                        scp -o StrictHostKeyChecking=no -r grafana/ ${DEPLOY_USER}@${DEPLOY_HOST}:/opt/finance-tracker/
                    """
                    // Запускаем docker-compose
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} \\
                        "cd /opt/finance-tracker && \\
                        docker-compose pull && \\
                        docker-compose up -d"
                    """
                }
            }
        }
    }
}