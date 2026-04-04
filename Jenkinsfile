pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                echo '📦 Checking out code from GitHub...'
                checkout scm
            }
        }
        
        stage('Docker Build') {
            steps {
                echo '🐳 Building Docker image...'
                sh 'docker build -t inventory-system:latest .'
            }
        }
        
        stage('Stop Old Container') {
            steps {
                echo '🛑 Stopping existing container...'
                sh '''
                    docker stop inventory-app 2>/dev/null || true
                    docker rm inventory-app 2>/dev/null || true
                '''
            }
        }
        
        stage('Deploy Container') {
            steps {
                echo '🚀 Starting new container...'
                sh 'docker run -d --name inventory-app -p 8081:8080 --restart unless-stopped inventory-system:latest'
            }
        }
        
        stage('Health Check') {
            steps {
                echo '🏥 Waiting for application to start...'
                sh 'sleep 10'
                sh 'curl -f http://localhost:8081/api/health'
            }
        }
        
        stage('Verify Deployment') {
            steps {
                echo '📊 Container status:'
                sh 'docker ps --filter name=inventory-app'
            }
        }
    }
    
    post {
        success {
            echo '========================================='
            echo '✅ PIPELINE COMPLETED SUCCESSFULLY!'
            echo '========================================='
            echo 'Jenkins: http://localhost:8082'
            echo 'Application: http://localhost:8081'
            echo '========================================='
        }
        failure {
            echo '========================================='
            echo '❌ PIPELINE FAILED!'
            echo '========================================='
            echo 'Check the logs above for errors.'
            echo '========================================='
        }
    }
}