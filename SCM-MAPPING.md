# SCM Activities Mapping for Inventory Management System

## 1. Configuration Identification
- Source code: src/main/java/
- Build config: pom.xml
- App config: application.properties (port 8081)
- Container config: Dockerfile
- Deployment config: ansible/playbook.yml, ansible/vars.yml
- Version control: Git repository

## 2. Configuration Control
- Version control via Git
- Build automation via Maven
- CI/CD via Jenkins (or manual pipeline)
- Container versioning via Docker tags
- Deployment control via Ansible

## 3. Configuration Status Accounting
- Git log: `git log --oneline`
- Build status: `mvn verify`
- Container status: `docker ps`
- Deployment logs: `ansible-playbook ... -vvv`
- App health: `curl http://localhost:8081/api/health`

## 4. Configuration Auditing
- Verify Git version: `git rev-parse HEAD`
- Verify Docker image: `docker images | grep inventory-system`
- Verify container: `docker inspect inventory-app`
- Verify deployment: `curl -f http://localhost:8081/api/health`
- Verify Ansible syntax: `ansible-playbook --syntax-check`