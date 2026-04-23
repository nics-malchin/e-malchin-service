package com.nics.e_malchin_service.repository;

import com.nics.e_malchin_service.Entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LabResultRepository extends JpaRepository<LabResult, Integer> {

    @Query("SELECT r FROM LabResult r WHERE r.livestock.id = :livestockId ORDER BY r.sampleDate DESC")
    List<LabResult> findByLivestockId(Integer livestockId);
}
