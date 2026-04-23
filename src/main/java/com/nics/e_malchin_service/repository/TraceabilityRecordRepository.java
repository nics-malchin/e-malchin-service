package com.nics.e_malchin_service.repository;

import com.nics.e_malchin_service.Entity.TraceabilityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TraceabilityRecordRepository extends JpaRepository<TraceabilityRecord, Integer> {

    @Query("SELECT r FROM TraceabilityRecord r WHERE r.livestock.id = :livestockId ORDER BY r.recordedAt ASC")
    List<TraceabilityRecord> findByLivestockId(Integer livestockId);
}
