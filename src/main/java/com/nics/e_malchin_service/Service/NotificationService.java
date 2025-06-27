package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.NotificationDAO;
import com.nics.e_malchin_service.Entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationDAO notificationDAO;

    public List<Notification> findAll(){
        return notificationDAO.findAll();
    }
}
