pipeline {
    agent any
    
    environment {
        APP_NAME = 'inventory-management-system'
        APP_PORT = '8081'
        DOCKER_IMAGE = 'inventory-system:latest'
        CONTAINER_NAME = 'inventory-app'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📦 Cloning repository...'
                checkout scm
                sh 'echo "Current commit: $(git log -1 --oneline)"'
            }
        }
        
        stage('Build Application') {
            steps {
                echo '🏗️ Building Spring Boot application...'
                sh 'mvn clean package -DskipTests'
            }
            post {
                success {
                    echo '✅ Build successful'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
                failure {
                    echo '❌ Build failed'
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                echo '🧪 Running unit tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                echo '🐳 Building Docker image...'
                sh 'docker build -t inventory-system:latest .'
                sh 'docker images | grep inventory-system'
            }
            post {
                success {
                    echo '✅ Docker image built successfully'
                }
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
        
        stage('Deploy with Ansible') {
            steps {
                echo '🚀 Deploying with Ansible...'
                sh '''
                    ansible-playbook ansible/playbook.yml \
                        -i ansible/inventory \
                        --become \
                        --skip-tags "python-docker,docker-user,app-build,test"
                '''
            }
            post {
                success {
                    echo '✅ Deployment successful'
                }
                failure {
                    echo '❌ Deployment failed'
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo '🏥 Checking application health...'
                script {
                    timeout(time: 60, unit: 'SECONDS') {
                        waitUntil {
                            try {
                                sh "curl -f http://localhost:${APP_PORT}/api/health"
                                return true
                            } catch (Exception e) {
                                echo 'Waiting for application to start...'
                                sleep 5
                                return false
                            }
                        }
                    }
                }
            }
        }
        
        stage('Verify Container') {
            steps {
                echo '📊 Container status:'
                sh 'docker ps --filter name=inventory-app'
                sh 'docker logs inventory-app --tail 20'
            }
        }
        
        stage('Integration Test') {
            steps {
                echo '🧪 Running integration tests...'
                sh '''
                    echo "Testing API endpoints..."
                    
                    echo "1. Health Check:"
                    curl -s http://localhost:8081/api/health
                    
                    echo "\\n2. Adding test inventory..."
                    curl -X POST http://localhost:8081/api/inventory \
                        -H "Content-Type: application/json" \
                        -d '{
                            "productId": "JENKINS-TEST",
                            "productName": "Jenkins Test Product",
                            "warehouseId": "WH-TEST",
                            "warehouseName": "Test Warehouse",
                            "quantity": 100,
                            "minThreshold": 10,
                            "maxThreshold": 200
                        }'
                    
                    echo "\\n3. Getting inventory..."
                    curl -s http://localhost:8081/api/inventory | python3 -m json.tool | head -20
                    
                    echo "\\n4. Dashboard stats:"
                    curl -s http://localhost:8081/api/dashboard/stats
                '''
            }
        }
    }
    
    post {
        success {
            echo '========================================='
            echo '✅ PIPELINE COMPLETED SUCCESSFULLY!'
            echo '========================================='
            echo "Application is running at: http://localhost:8081"
            echo "Health check: http://localhost:8081/api/health"
            echo '========================================='
        }
        failure {
            echo '========================================='
            echo '❌ PIPELINE FAILED!'
            echo '========================================='
            echo 'Check the logs above for errors.'
            echo 'Common issues:'
            echo '  - Docker daemon not running'
            echo '  - Port 8081 already in use'
            echo '  - Ansible syntax error'
            echo '========================================='
        }
        always {
            echo 'Pipeline execution completed.'
            cleanWs()
        }
    }
}