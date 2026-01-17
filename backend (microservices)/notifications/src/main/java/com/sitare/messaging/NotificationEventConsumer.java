package com.sitare.messaging;

import com.sitare.modal.Notification;
import com.sitare.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "tm-notification", groupId = "notification-service-group")
    public void sentBookingUpdateEvent(Notification notification){
        notificationService.createNotification(notification);
    }

}
