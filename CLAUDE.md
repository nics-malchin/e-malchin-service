# e-malchin-service — NICS Spring Boot Backend

## Role
This service is the single backend API for the Angular portal and Flutter mobile app.

It does not own authentication.
Keycloak owns authentication.

This service must validate Keycloak-issued JWT tokens and enforce role-based access.

## Authentication boundary
Permanent rules:
- do not add new code to `AuthController`
- do not extend password-grant proxy behavior
- long-term target is resource-server-only
- clients authenticate directly with Keycloak
- backend receives Bearer tokens and validates them

## Current migration status
- portal Keycloak migration is already underway / active
- `AuthController` is legacy and remove-candidate after client migration is complete
- old JWT helper/filter code is also cleanup-candidate if unused

## Security rules
- protect data exposure carefully
- do not return entities directly when they include internal or sensitive fields
- prefer DTO responses for profile and public API payloads
- `/api/user/me` must not expose password or internal-only fields

## Folder rules
Existing package naming stays as-is unless there is a strong reason to rename mid-flight.

For new code:
- repositories → `repository/`
- DTOs → `dto/`
- do not add new files into legacy `DAO/`

## MVP domain priorities
Add/refactor in this order:
1. `AnimalHealth`
2. `GpsDevice`
3. `LabResult`
4. `SoilSample`
5. `PhotoMonitoringPoint`
6. `Certificate`
7. `TraceabilityRecord`

All new entities must extend `BaseEntity`.

## Current implementation direction
- keep MySQL for MVP unless there is a concrete blocking geospatial need
- use Spring Security resource server
- keep `preferred_username -> local User lookup` boundary for now unless there is a reason to change it
- remove legacy auth config only after client usage is verified gone

## Response style
Explain in Mongolian.
Use English naming for code artifacts.
For each task:
- inspect actual current code first
- identify safest cleanup path
- avoid wide refactors without proof
- list exact files and risks