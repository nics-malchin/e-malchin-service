package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    // Хэрэгтэй бол энд custom query бичиж болно
}
