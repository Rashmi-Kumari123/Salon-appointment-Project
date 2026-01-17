package com.sitare.service.impl;

import com.sitare.mapper.NotificationMapper;
import com.sitare.modal.Notification;
import com.sitare.payload.dto.BookingDTO;
import com.sitare.payload.dto.NotificationDTO;
import com.sitare.repository.NotificationRepository;
import com.sitare.service.NotificationService;
import com.sitare.service.client.BookingGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final BookingGrpcClient bookingGrpcClient;
    private final RealTimeCommunicationService realTimeCommunicationService;

    @Override
    public NotificationDTO createNotification(Notification notification) {
        Notification savedNotification= notificationRepository.save(notification);
        BookingDTO bookingDTO = null;
        try {
            bookingDTO = bookingGrpcClient.getBookingById(savedNotification.getBookingId());
        } catch (Exception e) {
            // If booking fetch fails, continue with null
        }
        NotificationDTO notificationDTO= notificationMapper.toDTO(
                savedNotification,
                bookingDTO
        );
        realTimeCommunicationService.sendNotification(notificationDTO);
        return notificationDTO;
    }

    @Override
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }
    @Override
    public List<Notification> getAllNotificationsBySalonId(Long salonId) {
        return notificationRepository.findBySalonId(salonId);
    }
    @Override
    public Notification markNotificationAsRead(Long notificationId) throws Exception {
        return notificationRepository.findById(notificationId).map(notification -> {
            notification.setIsRead(true);
            return notificationRepository.save(notification);
        }).orElseThrow(() -> new Exception("Notification not found"));
    }

    @Override
    public void deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
        } else {
            throw new RuntimeException("Notification not found");
        }
    }
    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
