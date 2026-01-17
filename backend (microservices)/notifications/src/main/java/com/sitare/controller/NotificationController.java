package com.sitare.controller;

import com.sitare.mapper.NotificationMapper;
import com.sitare.modal.Notification;
import com.sitare.payload.dto.BookingDTO;
import com.sitare.payload.dto.NotificationDTO;
import com.sitare.service.NotificationService;
import com.sitare.service.client.BookingGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final BookingGrpcClient bookingGrpcClient;

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(
            @RequestBody Notification notification) {
        NotificationDTO createdNotification = notificationService
                .createNotification(notification);
        return ResponseEntity.ok(createdNotification);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(
            @PathVariable Long userId) {
        List<Notification> notifications = notificationService
                .getAllNotificationsByUserId(userId);

        List<NotificationDTO> notificationDTOS=notifications.stream().map((notification)-> {
            try {
                BookingDTO bookingDTO = bookingGrpcClient.getBookingById(notification.getBookingId());
                return notificationMapper.toDTO(notification, bookingDTO);
            } catch (Exception e) {
                return notificationMapper.toDTO(notification, null);
            }
        }).collect(Collectors.toList());
        return ResponseEntity.ok(notificationDTOS);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications() {
        List<Notification> notifications = notificationService
                .getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(
            @PathVariable Long notificationId) throws Exception {
        Notification updatedNotification = notificationService
                .markNotificationAsRead(notificationId);
        BookingDTO bookingDTO = null;
        try {
            bookingDTO = bookingGrpcClient.getBookingById(updatedNotification.getBookingId());
        } catch (Exception e) {
            // If booking fetch fails, continue with null
        }
        NotificationDTO notificationDTO= notificationMapper.toDTO(
                updatedNotification,
                bookingDTO
        );
        return ResponseEntity.ok(notificationDTO);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
