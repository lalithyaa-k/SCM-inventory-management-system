#!/bin/bash
set -e

echo "🚀 Deploying Inventory Management System with Ansible"

# Check Ansible installation
if ! command -v ansible &> /dev/null; then
    echo "❌ Ansible not found. Installing..."
    sudo apt update
    sudo apt install -y ansible
fi

# Run Ansible playbook
echo "Running Ansible playbook..."
ansible-playbook ansible/playbook.yml -i ansible/inventory -v

# Check deployment status
echo "Checking deployment status..."
sleep 5

if curl -s http://localhost:8080/api/health | grep -q "UP"; then
    echo "✅ Deployment successful!"
    echo "Application is running at: http://localhost:8080"
else
    echo "❌ Deployment failed!"
    exit 1
fi