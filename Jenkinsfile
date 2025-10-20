pipeline {
    agent any

    environment {
        // 🔐 Замени эти значения или задай через Jenkins Credentials + parameters
        DEPLOY_USER  = 'kali'           // ← твой пользователь на Kali/Ubuntu
        DEPLOY_HOST  = '192.168.0.110'       // ← IP твоего VPS (Kali)
        APP_NAME     = 'finance-tracker'
        IMAGE_NAME   = "finance-tracker:${env.BUILD_NUMBER}"
        DEPLOY_PATH  = '/opt/finance-tracker'
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
                    // Убедись, что Docker установлен и Jenkins в группе docker
                    docker.build(env.IMAGE_NAME)
                }
            }
        }

        stage('Push Image to VPS via Save/Load') {
            steps {
                script {
                    // Сохраняем образ в tar-файл
                    sh "docker save ${env.IMAGE_NAME} -o ${env.IMAGE_NAME}.tar"

                    // Создаём папку на VPS и копируем всё
                    sh """
                        ssh -o StrictHostKeyChecking=no ${env.DEPLOY_USER}@${env.DEPLOY_HOST} \\
                        "mkdir -p ${env.DEPLOY_PATH}"
                    """

                    sh """
                        scp -o StrictHostKeyChecking=no ${env.IMAGE_NAME}.tar \\
                        docker-compose.yml \\
                        prometheus.yml \\
                        ${env.DEPLOY_USER}@${env.DEPLOY_HOST}:${env.DEPLOY_PATH}/
                    """

                    // Копируем папку grafana (если существует)
                    sh """
                        if [ -d grafana ]; then
                            scp -o StrictHostKeyChecking=no -r grafana/ \\
                            ${env.DEPLOY_USER}@${env.DEPLOY_HOST}:${env.DEPLOY_PATH}/
                        fi
                    """
                }
            }
        }

        stage('Deploy on VPS') {
            steps {
                script {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${env.DEPLOY_USER}@${env.DEPLOY_HOST} \\
                        "cd ${env.DEPLOY_PATH} && \\
                        docker load -i ${env.IMAGE_NAME}.tar && \\
                        docker-compose down && \\
                        docker-compose up -d"
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Успешный деплой FinanceTracker! Версия: ${env.BUILD_NUMBER}"
        }
        failure {
            echo "❌ Сборка или деплой завершились с ошибкой."
        }
        always {
            // Опционально: удаляем временный tar-файл
            sh "rm -f ${env.IMAGE_NAME}.tar || true"
        }
    }
}