package com.sitare.service.client;
import com.sitare.domain.BookingStatus;
import com.sitare.grpc.*;
import com.sitare.payload.dto.BookingDTO;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class BookingGrpcClient {

    @GrpcClient("booking-service")
    private BookingServiceGrpc.BookingServiceBlockingStub bookingServiceStub;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public BookingDTO getBookingById(Long bookingId) {
        try {
            GetBookingByIdRequest request = GetBookingByIdRequest.newBuilder()
                    .setBookingId(bookingId)
                    .build();
            
            BookingResponse response = bookingServiceStub.getBookingById(request);
            
            BookingDTO bookingDTO = new BookingDTO();
            bookingDTO.setId(response.getId());
            bookingDTO.setSalonId(response.getSalonId());
            bookingDTO.setCustomerId(response.getCustomerId());
            bookingDTO.setTotalPrice(response.getTotalPrice());
            
            if (!response.getStartTime().isEmpty()) {
                bookingDTO.setStartTime(LocalDateTime.parse(response.getStartTime(), DATE_TIME_FORMATTER));
            }
            
            if (!response.getEndTime().isEmpty()) {
                bookingDTO.setEndTime(LocalDateTime.parse(response.getEndTime(), DATE_TIME_FORMATTER));
            }
            
            if (response.getServiceIdsCount() > 0) {
                Set<Long> serviceIds = new HashSet<>(response.getServiceIdsList());
                bookingDTO.setServicesIds(serviceIds);
            }
            
            if (!response.getStatus().isEmpty()) {
                try {
                    bookingDTO.setStatus(BookingStatus.valueOf(response.getStatus()));
                } catch (IllegalArgumentException e) {
                    // If status doesn't match enum, leave it null
                }
            }
            return bookingDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching booking from gRPC: " + e.getMessage(), e);
        }
    }
}
