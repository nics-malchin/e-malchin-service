package com.nics.e_malchin_service.util;

import com.nics.e_malchin_service.Entity.BaseEntity;

import java.time.LocalDateTime;

/**
 * Utility class that helps to populate {@link BaseEntity} audit fields when an entity is
 * created or updated. Most of the domain entities extend {@link BaseEntity} and the
 * application frequently persists them without explicitly setting audit metadata. In order to
 * avoid duplicate code (and null pointer exceptions caused by missing audit values) the helper
 * below centralises that logic.
 */
public final class EntityAuditUtil {

    private static final int SYSTEM_USER_ID = 0;

    private EntityAuditUtil() {
        // Utility class
    }

    public static <T extends BaseEntity> void applyCreateAuditValues(T entity) {
        applyCreateAuditValues(entity, null);
    }

    public static <T extends BaseEntity> void applyCreateAuditValues(T entity, Integer actorId) {
        int resolvedActor = resolveActor(actorId, entity);

        if (entity.getCreatedBy() == null) {
            entity.setCreatedBy(resolvedActor);
        }
        if (entity.getUpdatedBy() == null) {
            entity.setUpdatedBy(resolvedActor);
        }
        if (entity.getCreatedDate() == null) {
            entity.setCreatedDate(LocalDateTime.now());
        }
        entity.setUpdatedDate(LocalDateTime.now());

        if (entity.getActiveFlag() == null) {
            entity.setActiveFlag(true);
        }
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
    }

    public static <T extends BaseEntity> void applyUpdateAuditValues(T entity) {
        applyUpdateAuditValues(entity, null);
    }

    public static <T extends BaseEntity> void applyUpdateAuditValues(T entity, Integer actorId) {
        int resolvedActor = resolveActor(actorId, entity);
        entity.setUpdatedBy(resolvedActor);
        entity.setUpdatedDate(LocalDateTime.now());
    }

    private static int resolveActor(Integer actorId, BaseEntity entity) {
        if (actorId != null) {
            return actorId;
        }
        if (entity.getUpdatedBy() != null) {
            return entity.getUpdatedBy();
        }
        if (entity.getCreatedBy() != null) {
            return entity.getCreatedBy();
        }
        return SYSTEM_USER_ID;
    }
}

