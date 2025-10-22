pipeline {
    agent any

    environment {
        DEPLOY_USER  = 'kali'
        DEPLOY_HOST  = '10.203.239.11'
        DEPLOY_PATH  = '/home/kali/finance-tracker'
        IMAGE_NAME   = 'finance-tracker:latest'  // ← фиксированный тег
        TAR_NAME     = 'finance-tracker.tar'     // ← без номера сборки в имени
    }

    tools {
        maven 'Maven-3.9'
        jdk  'JDK-21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -B'
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
                    docker.build(env.IMAGE_NAME)
                }
            }
        }

        stage('Push Image to VPS via Save/Load') {
            steps {
                script {
                    // Сохраняем образ как finance-tracker.tar
                    sh "docker save ${env.IMAGE_NAME} -o ${env.TAR_NAME}"

                    // Создаём папку на VPS
                    sh """
                        ssh -o StrictHostKeyChecking=no ${env.DEPLOY_USER}@${env.DEPLOY_HOST} \\
                        "mkdir -p ${env.DEPLOY_PATH}"
                    """

                    // Копируем файлы
                    sh """
                        scp -o StrictHostKeyChecking=no \\
                            ${env.TAR_NAME} \\
                            docker-compose.yml \\
                            prometheus.yml \\
                            ${env.DEPLOY_USER}@${env.DEPLOY_HOST}:${env.DEPLOY_PATH}/
                    """

                    // Копируем grafana, если есть
                    if (sh(script: '[ -d grafana ] && echo "exists" || echo "missing"', returnStdout: true).trim() == 'exists') {
                        sh "scp -o StrictHostKeyChecking=no -r grafana/ ${env.DEPLOY_USER}@${env.DEPLOY_HOST}:${env.DEPLOY_PATH}/"
                    }
                }
            }
        }

        stage('Deploy on VPS') {
            steps {
                script {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${env.DEPLOY_USER}@${env.DEPLOY_HOST} \\
                        "cd ${env.DEPLOY_PATH} && \\
                        docker load -i ${env.TAR_NAME} && \\
                        docker-compose down && \\
                        docker-compose up -d"
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Успешный деплой FinanceTracker! Используется образ: ${env.IMAGE_NAME}"
        }
        failure {
            echo "❌ Сборка или деплой завершились с ошибкой."
        }
        always {
            sh "rm -f ${env.TAR_NAME} || true"
        }
    }
}