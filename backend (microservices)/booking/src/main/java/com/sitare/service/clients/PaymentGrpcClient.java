package com.sitare.service.clients;

import com.razorpay.RazorpayException;
import com.sitare.domain.BookingStatus;
import com.sitare.domain.PaymentMethod;
import com.sitare.exception.UserException;
import com.sitare.grpc.*;
import com.sitare.modal.Booking;
import com.sitare.payload.response.PaymentLinkResponse;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentGrpcClient {
    @GrpcClient("payment")
    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceStub;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public PaymentLinkResponse createPaymentLink(String jwt, Booking booking, PaymentMethod paymentMethod) 
            throws UserException, RazorpayException {
        try {
            // Convert Booking to BookingMessage
            BookingMessage.Builder bookingMsgBuilder = BookingMessage.newBuilder()
                    .setId(booking.getId())
                    .setSalonId(booking.getSalonId())
                    .setCustomerId(booking.getCustomerId())
                    .setStartTime(booking.getStartTime() != null ? 
                            booking.getStartTime().format(DATE_TIME_FORMATTER) : "")
                    .setEndTime(booking.getEndTime() != null ? 
                            booking.getEndTime().format(DATE_TIME_FORMATTER) : "")
                    .addAllServiceIds(booking.getServiceIds() != null ? booking.getServiceIds() : new ArrayList<>())
                    .setStatus(booking.getStatus() != null ? booking.getStatus().name() : BookingStatus.PENDING.name())
                    .setTotalPrice(booking.getTotalPrice());
            
            CreatePaymentLinkRequest request = CreatePaymentLinkRequest.newBuilder()
                    .setJwtToken(jwt)
                    .setBooking(bookingMsgBuilder.build())
                    .setPaymentMethod(paymentMethod.name())
                    .build();
            com.sitare.grpc.PaymentLinkResponse response = paymentServiceStub.createPaymentLink(request);
            PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();
            paymentLinkResponse.setPayment_link_url(response.getPaymentLinkUrl());
            paymentLinkResponse.setPayment_link_id(response.getPaymentLinkId());
            return paymentLinkResponse;
        } catch (Exception e) {
            if (e instanceof RazorpayException) {
                throw (RazorpayException) e;
            }
            throw new UserException("Error creating payment link from gRPC: " + e.getMessage());
        }
    }
}
