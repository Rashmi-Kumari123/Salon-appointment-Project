package com.sitare.grpc;

import com.razorpay.RazorpayException;
import com.sitare.domain.BookingStatus;
import com.sitare.domain.PaymentMethod;
import com.sitare.exception.UserException;
import com.sitare.payload.dto.BookingDTO;
import com.sitare.payload.dto.UserDTO;
import com.sitare.service.PaymentService;
import com.sitare.service.clients.UserGrpcClient;
import com.stripe.exception.StripeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@GrpcService
@RequiredArgsConstructor
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final PaymentService paymentService;
    private final UserGrpcClient userGrpcClient;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void createPaymentLink(CreatePaymentLinkRequest request, StreamObserver<PaymentLinkResponse> responseObserver) {
        try {
            String jwt = request.getJwtToken();
            BookingMessage bookingMsg = request.getBooking();
            PaymentMethod paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod());
            
            // Get user from JWT
            UserDTO user = userGrpcClient.getUserFromJwtToken(jwt);
            
            // Convert BookingMessage to BookingDTO
            BookingDTO bookingDTO = convertToBookingDTO(bookingMsg);
            
            com.sitare.payload.response.PaymentLinkResponse paymentLinkResponse = 
                    paymentService.createOrder(user, bookingDTO, paymentMethod);
            
            PaymentLinkResponse response = PaymentLinkResponse.newBuilder()
                    .setPaymentLinkUrl(paymentLinkResponse.getPayment_link_url() != null ? 
                            paymentLinkResponse.getPayment_link_url() : "")
                    .setPaymentLinkId(paymentLinkResponse.getPayment_link_id() != null ? 
                            paymentLinkResponse.getPayment_link_id() : "")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT
                    .withDescription("Invalid payment method: " + e.getMessage())
                    .asRuntimeException());
        } catch (UserException | RazorpayException | StripeException e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error creating payment link: " + e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Unexpected error: " + e.getMessage())
                    .asRuntimeException());
        }
    }
    
    private BookingDTO convertToBookingDTO(BookingMessage bookingMsg) {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(bookingMsg.getId());
        bookingDTO.setSalonId(bookingMsg.getSalonId());
        bookingDTO.setCustomerId(bookingMsg.getCustomerId());
        
        if (!bookingMsg.getStartTime().isEmpty()) {
            bookingDTO.setStartTime(LocalDateTime.parse(bookingMsg.getStartTime(), DATE_TIME_FORMATTER));
        }
        
        if (!bookingMsg.getEndTime().isEmpty()) {
            bookingDTO.setEndTime(LocalDateTime.parse(bookingMsg.getEndTime(), DATE_TIME_FORMATTER));
        }
        
        Set<Long> serviceIds = new HashSet<>(bookingMsg.getServiceIdsList());
        bookingDTO.setServicesIds(serviceIds);
        
        if (!bookingMsg.getStatus().isEmpty()) {
            try {
                bookingDTO.setStatus(BookingStatus.valueOf(bookingMsg.getStatus()));
            } catch (IllegalArgumentException e) {
                // Default to PENDING if status is invalid
                bookingDTO.setStatus(BookingStatus.PENDING);
            }
        }
        bookingDTO.setTotalPrice(bookingMsg.getTotalPrice());
        return bookingDTO;
    }
}
