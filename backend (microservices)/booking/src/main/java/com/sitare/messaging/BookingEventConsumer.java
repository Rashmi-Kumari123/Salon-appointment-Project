package com.sitare.messaging;

import com.sitare.modal.PaymentOrder;
import com.sitare.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventConsumer {
    private final BookingService bookingService;
    @KafkaListener(topics = "tm-notification", groupId = "booking-service-group")
    public void bookingUpdateListener(PaymentOrder paymentOrder){
        System.out.println("Received message: " + paymentOrder);
        bookingService.bookingSucess(paymentOrder);
        System.out.println("Received message: " + paymentOrder);
    }
}
