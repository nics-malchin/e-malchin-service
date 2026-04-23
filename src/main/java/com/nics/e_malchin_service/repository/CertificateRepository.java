package com.nics.e_malchin_service.repository;

import com.nics.e_malchin_service.Entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    @Query("SELECT c FROM Certificate c WHERE c.livestock.id = :livestockId ORDER BY c.issuedDate DESC")
    List<Certificate> findByLivestockId(Integer livestockId);
}
