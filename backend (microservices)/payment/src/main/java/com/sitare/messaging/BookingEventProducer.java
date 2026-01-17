package com.sitare.messaging;

import com.sitare.modal.PaymentOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventProducer {

    private final KafkaTemplate<String, PaymentOrder> kafkaTemplate;

    public void sentBookingUpdateEvent(PaymentOrder paymentOrder){
        kafkaTemplate.send("tm-notification", paymentOrder);
    }
}
