package com.sitare.modal;

import com.sitare.domain.PaymentMethod;
import com.sitare.domain.PaymentOrderStatus;
import lombok.Data;

@Data
public class PaymentOrder {
    private Long id;
    private Long amount;
    private PaymentOrderStatus status;
    private PaymentMethod paymentMethod;
    private String paymentLinkId;
    private Long userId;
    private Long bookingId;
}
