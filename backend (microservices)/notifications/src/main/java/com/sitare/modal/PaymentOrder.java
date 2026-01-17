package com.sitare.modal;
import com.sitare.domain.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentOrder {
    private Long id;
    private Long amount;
    private PaymentMethod paymentMethod;
    private String paymentLinkId;
    private Long userId;
    private Long bookingId;
    private Long salonId;
}
