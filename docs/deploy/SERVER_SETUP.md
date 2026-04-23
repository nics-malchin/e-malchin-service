# Server Setup

This document prepares the production host for GitHub Actions based deployment.

## 1. Create Runtime Directories

```bash
sudo mkdir -p /opt/emalchin/env
sudo mkdir -p /opt/backups/e-malchin
sudo chown -R cloudmn:cloudmn /opt/emalchin
```

## 2. Create Backend Runtime Env File

Create `/opt/emalchin/env/api.production.env`:

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://192.168.100.10:3306/dbmaclhin?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ulaanbaatar&characterEncoding=utf8
SPRING_DATASOURCE_USERNAME=sysmalchin
SPRING_DATASOURCE_PASSWORD=replace_me
KEYCLOAK_CLIENT_ID=e-malchin-app
KEYCLOAK_CLIENT_SECRET=replace_me
KEYCLOAK_AUTH_SERVER_URL=https://auth.nics-malchin.mn
KEYCLOAK_REALM=nics
KEYCLOAK_ADMIN_USERNAME=admin
KEYCLOAK_ADMIN_PASSWORD=replace_me
```

## 3. Create Deploy Env File

Create `/opt/emalchin/env/stack.production.env`:

```bash
API_SERVICE_NAME=e-malchin-service
GUI_SERVICE_NAME=portal-malchin-gui
APP_ENV_FILE=/opt/emalchin/env/api.production.env
```

If your swarm service names differ, update the values above to match `docker service ls`.

## 4. Install Self-Hosted Runner

Create a dedicated runner user if needed, then install the runner from GitHub repository settings.

Runner labels should include:

- `self-hosted`
- `linux`
- `prod`

The runner user must be able to run Docker commands:

```bash
sudo usermod -aG docker <runner-user>
newgrp docker
```

If Docker group access is not used, the workflow must call Docker via `sudo`.

## 5. Registry Access

The server must be able to pull from GHCR.

```bash
echo <ghcr-token> | docker login ghcr.io -u <github-user> --password-stdin
```

Use a GitHub token with package read permission.

## 6. Pre-Deploy Backup Command

Run before every production release:

```bash
sudo docker exec $(docker ps --format '{{.Names}}' | grep '^mysql') \
  mysqldump -usysmalchin -p'replace_me' \
  --single-transaction --routines --triggers --events dbmaclhin \
  > /opt/backups/e-malchin/dbmaclhin_$(date +%F_%H%M%S).sql
```

## 7. Manual Smoke Test After Deploy

```bash
docker service ps e-malchin-service
docker service ps portal-malchin-gui
sudo docker service logs e-malchin-service --since 10m
```
