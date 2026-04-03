pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Docker Deploy') {
            steps {
                bat '''
                    cd C:\\Users\\lalithyaa\\inventory-management-system
                    docker build -t inventory-system:latest .
                    docker stop inventory-app 2>nul || exit 0
                    docker rm inventory-app 2>nul || exit 0
                    docker run -d --name inventory-app -p 8081:8080 inventory-system:latest
                '''
            }
        }
        
        stage('Health Check') {
            steps {
                bat 'timeout /t 10'
                bat 'curl http://localhost:8081/api/health'
            }
        }
    }
}