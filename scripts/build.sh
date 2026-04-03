#!/bin/bash
set -e

echo "🚀 Building Inventory Management System"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check prerequisites
check_prerequisites() {
    echo "Checking prerequisites..."
    
    if ! command -v java &> /dev/null; then
        echo -e "${RED}Java not found. Please install JDK 17${NC}"
        exit 1
    fi
    
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}Maven not found. Please install Maven${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✓ Prerequisites satisfied${NC}"
}

# Build application
build_app() {
    echo "Building application..."
    mvn clean compile
    mvn test
    mvn package -DskipTests
    
    if [ -f "target/*.jar" ]; then
        echo -e "${GREEN}✓ Build successful${NC}"
    else
        echo -e "${RED}✗ Build failed${NC}"
        exit 1
    fi
}

# Run application locally
run_local() {
    echo "Starting application locally..."
    java -jar target/*.jar &
    APP_PID=$!
    
    echo "Application started with PID: $APP_PID"
    echo "Waiting for application to be ready..."
    
    sleep 10
    
    if curl -s http://localhost:8080/api/health > /dev/null; then
        echo -e "${GREEN}✓ Application is running${NC}"
        echo "Access at: http://localhost:8080"
    else
        echo -e "${RED}✗ Application failed to start${NC}"
        kill $APP_PID
        exit 1
    fi
}

# Main execution
main() {
    check_prerequisites
    build_app
    
    if [ "$1" == "--run" ]; then
        run_local
    fi
}

main "$@"