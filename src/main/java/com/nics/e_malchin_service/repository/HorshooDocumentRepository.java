package com.nics.e_malchin_service.repository;

import com.nics.e_malchin_service.Entity.HorshooDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorshooDocumentRepository extends JpaRepository<HorshooDocument, Integer> {
    List<HorshooDocument> findByHorshooIdAndActiveFlagTrueOrderByCreatedDateDesc(Integer horshooId);
}
