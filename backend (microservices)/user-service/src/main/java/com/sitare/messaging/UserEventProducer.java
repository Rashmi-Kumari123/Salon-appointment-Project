package com.sitare.messaging;

import com.sitare.payload.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventProducer {
    private final KafkaTemplate<String, UserDTO> kafkaTemplate;

    public void userCreatedEvent(UserDTO userDTO){
        kafkaTemplate.send("tm-notification", userDTO);
    }
}
