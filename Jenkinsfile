pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        APP_DIR = 'ats-user'
        APP_NAME = 'ats-user'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Clonando repositorio ats-usuarios'
                git branch: 'feature/develop', url: 'https://github.com/tuttodev27/ats-usuarios.git'
            }
        }

        stage('Prepare') {
            steps {
                echo 'Dando permisos a gradlew'
                dir("${APP_DIR}") {
                    sh 'chmod +x gradlew'
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Ejecutando tests'
                dir("${APP_DIR}") {
                    sh './gradlew test'
                }
            }
        }

        stage('Build') {
            steps {
                echo 'Compilando proyecto'
                dir("${APP_DIR}") {
                    sh './gradlew clean build'
                }
            }
        }

        stage('BootJar') {
            steps {
                echo 'Generando bootJar'
                dir("${APP_DIR}") {
                    sh './gradlew bootJar'
                }
            }
        }

        stage('Archive Artifact') {
            steps {
                echo 'Guardando artefacto en Jenkins'
                archiveArtifacts artifacts: 'ats-user/build/libs/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Pipeline ejecutado correctamente para ats-user'
        }
        failure {
            echo 'El pipeline falló'
        }
        always {
            echo 'Fin del pipeline'
        }
    }
}