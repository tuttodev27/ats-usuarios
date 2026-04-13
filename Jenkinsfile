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
        stage('Prepare') {
            steps {
                echo 'Usando el checkout provisto por Jenkins y dando permisos a gradlew'
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
