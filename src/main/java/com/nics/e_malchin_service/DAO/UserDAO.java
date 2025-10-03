package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {

    @Query("select a from User a")
    List<User> findAll();


    Optional<User> findByUsername(String username);

    User findById(Long userId);
}
