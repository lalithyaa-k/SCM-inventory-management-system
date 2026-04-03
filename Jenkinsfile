pipeline {
    agent any

    environment {
        // Application Configuration
        APP_NAME = 'inventory-management-system'
        DOCKER_IMAGE = "inventory-system:${BUILD_NUMBER}"
        ANSIBLE_INVENTORY = 'ansible/inventory'
        ANSIBLE_PLAYBOOK = 'ansible/playbook.yml'
        
        // Credentials
        DOCKER_CREDS = credentials('docker-hub-credentials')
        GIT_CREDS = credentials('git-credentials')
    }

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    stages {
        stage('SCM Checkout') {
            steps {
                echo '📦 Cloning repository...'
                checkout scm
                sh 'git log -1 --pretty=%B > commit_message.txt'
                script {
                    def commitMsg = readFile('commit_message.txt').trim()
                    echo "Commit: ${commitMsg}"
                }
            }
        }

        stage('Code Quality Analysis') {
            steps {
                echo '🔍 Running code quality checks...'
                sh '''
                    echo "Checking code formatting..."
                    find src -name "*.java" -exec grep -l "System.out.println" {} \\; && echo "WARNING: System.out.println found" || true
                '''
            }
        }

        stage('Unit Tests') {
            steps {
                echo '🧪 Running unit tests...'
                sh 'mvn clean test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Application') {
            steps {
                echo '🏗️ Building application...'
                sh 'mvn clean package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo '🐳 Building Docker image...'
                sh """
                    docker build -t ${DOCKER_IMAGE} .
                    docker tag ${DOCKER_IMAGE} ${APP_NAME}:latest
                """
            }
        }

        stage('Docker Security Scan') {
            steps {
                echo '🔒 Scanning Docker image for vulnerabilities...'
                sh '''
                    docker scout quickview ${DOCKER_IMAGE} || echo "Docker Scout not available"
                    trivy image --severity HIGH,CRITICAL --no-progress ${DOCKER_IMAGE} || echo "Trivy not installed"
                '''
            }
        }

        stage('Push to Registry') {
            steps {
                echo '📤 Pushing Docker image to registry...'
                withDockerRegistry(credentialsId: 'docker-hub', toolName: 'docker') {
                    sh """
                        docker push ${DOCKER_IMAGE}
                        docker push ${APP_NAME}:latest
                    """
                }
            }
        }

        stage('Ansible Deployment') {
            steps {
                echo '🚀 Deploying with Ansible...'
                sh '''
                    ansible-playbook -i ${ANSIBLE_INVENTORY} ${ANSIBLE_PLAYBOOK} \
                        --extra-vars "app_version=${BUILD_NUMBER} docker_image=${DOCKER_IMAGE}"
                '''
            }
        }

        stage('Integration Tests') {
            steps {
                echo '🧪 Running integration tests...'
                script {
                    // Wait for application to be ready
                    timeout(time: 60, unit: 'SECONDS') {
                        waitUntil {
                            try {
                                sh 'curl -f http://localhost:8080/api/health'
                                return true
                            } catch (Exception e) {
                                return false
                            }
                        }
                    }
                    
                    // Run API tests
                    sh '''
                        echo "Testing API endpoints..."
                        curl -s http://localhost:8080/api/health | jq .
                        curl -s http://localhost:8080/api/inventory | jq '. | length'
                    '''
                }
            }
        }

        stage('Performance Test') {
            steps {
                echo '⚡ Running performance tests...'
                sh '''
                    echo "Load testing with Apache Bench..."
                    ab -n 100 -c 10 http://localhost:8080/api/health || echo "ab not installed"
                '''
            }
        }

        stage('Rollback Check') {
            when {
                expression { currentBuild.result == 'FAILURE' }
            }
            steps {
                echo '⚠️ Deployment failed, initiating rollback...'
                sh '''
                    echo "Rolling back to previous version..."
                    docker stop inventory-app || true
                    docker rm inventory-app || true
                    docker run -d --name inventory-app -p 8080:8080 inventory-system:previous
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
            emailext (
                subject: "SUCCESS: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: "The build was successful.\n\nBuild URL: ${env.BUILD_URL}",
                to: 'team@example.com'
            )
        }
        failure {
            echo '❌ Pipeline failed!'
            emailext (
                subject: "FAILURE: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: "The build failed. Please check the logs.\n\nBuild URL: ${env.BUILD_URL}",
                to: 'team@example.com'
            )
        }
        always {
            cleanWs()
        }
    }
}