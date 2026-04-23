# Release Checklist

## Before Any Deploy

- Confirm the exact Git commits for backend and frontend.
- Confirm the target image tags.
- Confirm production backup path and free disk space.
- Confirm staging is using a fresh production-like backup.
- Confirm secrets are present on the server.

## Staging Sign-Off

- API starts without schema errors.
- GUI loads and points to staging API.
- Authentication succeeds.
- Core CRUD flows work.
- No critical errors in container logs.

## Production Sign-Off

- Backup completed successfully.
- Previous image tags recorded.
- Deployment window agreed.
- Rollback command prepared.

## After Deploy

- Check `docker service ls`
- Check `docker service ps <service>`
- Check API health endpoint
- Check login flow
- Check 2 to 3 high-risk business screens
- Check application logs for the first 15 minutes
