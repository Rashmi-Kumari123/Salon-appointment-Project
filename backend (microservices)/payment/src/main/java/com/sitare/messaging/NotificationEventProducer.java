package com.sitare.messaging;
import com.sitare.payload.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventProducer {
    private final KafkaTemplate<String, NotificationDTO> kafkaTemplate;

    public void sentNotificationEvent(Long bookingId,
                                      Long userId,
                                      Long salonId) {
        NotificationDTO notification=new NotificationDTO();
        notification.setBookingId(bookingId);
        notification.setSalonId(salonId);
        notification.setUserId(userId);
        notification.setDescription("new booking got confirmed");
        notification.setType("BOOKING");

        kafkaTemplate.send("tm-notification", notification);

    }
}
