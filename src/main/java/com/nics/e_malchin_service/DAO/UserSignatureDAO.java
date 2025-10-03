package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.UserSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserSignatureDAO extends JpaRepository<UserSignature, Integer> {
    @Query("select a from UserSignature a where a.userId = ?1")
    UserSignature findUserSignatureByUserId(Integer id);
}
