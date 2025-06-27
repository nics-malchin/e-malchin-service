package com.nics.e_malchin_service.DAO;

import com.nics.e_malchin_service.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationDAO extends JpaRepository<Notification, Integer> {

    @Query("select a from Notification a")
    List<Notification> findAll();
}
