package com.nics.e_malchin_service.Service;

import com.nics.e_malchin_service.DAO.NotificationDAO;
import com.nics.e_malchin_service.DAO.UserDAO;
import com.nics.e_malchin_service.Entity.Notification;
import com.nics.e_malchin_service.Entity.User;
import com.nics.e_malchin_service.util.EntityAuditUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationDAO notificationDAO;
    @Autowired
    private UserDAO userDAO;

    public List<Notification> findAll(){
        return notificationDAO.findAll();
    }

    public Notification create(Notification notification) {
        if (notification.getUser() != null && notification.getUser().getId() != null) {
            User user = userDAO.findById(notification.getUser().getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id " + notification.getUser().getId()));
            notification.setUser(user);
        }
        EntityAuditUtil.applyCreateAuditValues(notification);
        return notificationDAO.save(notification);
    }

    public Notification update(Integer id, Notification request) {
        Notification existing = notificationDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found with id " + id));

        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            existing.setType(request.getType());
        }
        if (request.getUser() != null && request.getUser().getId() != null) {
            User user = userDAO.findById(request.getUser().getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id " + request.getUser().getId()));
            existing.setUser(user);
        }

        EntityAuditUtil.applyUpdateAuditValues(existing, request.getUpdatedBy());
        return notificationDAO.save(existing);
    }

    public void delete(Integer id) {
        if (!notificationDAO.existsById(id)) {
            throw new EntityNotFoundException("Notification not found with id " + id);
        }
        notificationDAO.deleteById(id);
    }
}
