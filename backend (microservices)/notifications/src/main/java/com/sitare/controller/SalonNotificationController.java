package com.sitare.controller;

import com.sitare.mapper.NotificationMapper;
import com.sitare.modal.Notification;
import com.sitare.payload.dto.BookingDTO;
import com.sitare.payload.dto.NotificationDTO;
import com.sitare.service.NotificationService;
import com.sitare.service.client.BookingGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/notifications/salon-owner")
@RequiredArgsConstructor
public class SalonNotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final BookingGrpcClient bookingGrpcClient;

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsBySalonId(
            @PathVariable Long salonId) {
        List<Notification> notifications = notificationService
                .getAllNotificationsBySalonId(salonId);
        List<NotificationDTO> notificationDTOS=notifications
                .stream()
                .map((notification)-> {
                    try {
                        BookingDTO bookingDTO = bookingGrpcClient.getBookingById(notification.getBookingId());
                        return notificationMapper.toDTO(notification, bookingDTO);
                    } catch (Exception e) {
                        return notificationMapper.toDTO(notification, null);
                    }
                }).collect(Collectors.toList());
        return ResponseEntity.ok(notificationDTOS);
    }
}
