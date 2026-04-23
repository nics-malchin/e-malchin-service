# Deployment Runbook

This project is currently deployed to a Docker Swarm host and must be updated without touching production data.

The release process is intentionally split into stages:

1. Back up production state.
2. Restore that backup into staging.
3. Validate the new backend and frontend against staging data.
4. Deploy immutable images to production with manual approval.

## Current Constraints

- Production MySQL data must be preserved.
- The application currently has no schema migration framework.
- Production secrets must not live in Git.
- Docker image tags must be immutable.

## Required One-Time Improvements

1. Move production secrets from `application-*.properties` into runtime environment variables or Docker secrets.
2. Introduce Flyway for versioned database migrations.
3. Build images in CI and deploy only tested tags.
4. Require manual approval before production rollout.

## Safe Release Flow

### 1. Production Backup

Run on the server before every release:

```bash
sudo mkdir -p /opt/backups/e-malchin
sudo docker exec mysql.1.xzw6oacdhcqkiktukvd1s0a31 \
  mysqldump -usysmalchin -p'<PASSWORD>' \
  --single-transaction --routines --triggers --events dbmaclhin \
  > /opt/backups/e-malchin/dbmaclhin_$(date +%F_%H%M%S).sql
```

Also export:

- active swarm stack file
- Docker service definitions
- nginx config
- current image tags

### 2. Staging Restore

Restore the latest dump into a staging MySQL instance, then point staging backend to that restored database.

Validation checklist:

- backend starts successfully
- login via Keycloak works
- high-risk pages load
- create/update flows succeed
- background jobs and email integrations do not fail

### 3. Production Deploy

Deploy only after staging passes:

```bash
docker service update --image ghcr.io/nics-malchin/e-malchin-service:<tag> emalchin_api
docker service update --image ghcr.io/nics-malchin/portal-malchin-gui:<tag> emalchin_gui
```

Use exact tags, never `latest`.

### 4. Rollback

Rollback is image-based first:

```bash
docker service update --image ghcr.io/nics-malchin/e-malchin-service:<previous-tag> emalchin_api
docker service update --image ghcr.io/nics-malchin/portal-malchin-gui:<previous-tag> emalchin_gui
```

If the issue is data-related, restore the production database dump only after confirming the rollback plan and downtime window.

## Recommended Branch Strategy

- `master` or `main`: protected production-ready branch
- `develop`: integration branch
- `release/*`: optional stabilization branch when many changes are pending

## Recommended Automation

- backend repo builds and publishes API image
- frontend repo builds and publishes GUI image
- deployment repo or backend repo stores swarm stack descriptors
- GitHub Actions deploys automatically to staging
- production deploy requires manual approval

## Secrets To Move Out Of Source

The current codebase contains production credentials in properties files. These must be rotated after migration to secrets.

At minimum move:

- MySQL URL, username, password
- Keycloak client secret
- Keycloak admin password
- any mail or Google API credentials
