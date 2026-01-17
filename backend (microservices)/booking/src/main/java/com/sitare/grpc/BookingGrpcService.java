package com.sitare.grpc;

import com.sitare.domain.BookingStatus;
import com.sitare.modal.Booking;
import com.sitare.service.BookingService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import java.time.format.DateTimeFormatter;

@GrpcService
@RequiredArgsConstructor
public class BookingGrpcService extends BookingServiceGrpc.BookingServiceImplBase {

    private final BookingService bookingService;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void getBookingById(GetBookingByIdRequest request, StreamObserver<BookingResponse> responseObserver) {
        try {
            Long bookingId = request.getBookingId();
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking == null) {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Booking not found with id: " + bookingId)
                        .asRuntimeException());
                return;
            }
            BookingResponse.Builder builder = BookingResponse.newBuilder()
                    .setId(booking.getId())
                    .setSalonId(booking.getSalonId())
                    .setCustomerId(booking.getCustomerId())
                    .setStatus(booking.getStatus() != null ? booking.getStatus().name() : "")
                    .setTotalPrice(booking.getTotalPrice());

            if (booking.getStartTime() != null) {
                builder.setStartTime(booking.getStartTime().format(DATE_TIME_FORMATTER));
            } else {
                builder.setStartTime("");
            }
            if (booking.getEndTime() != null) {
                builder.setEndTime(booking.getEndTime().format(DATE_TIME_FORMATTER));
            } else {
                builder.setEndTime("");
            }
            if (booking.getServiceIds() != null) {
                builder.addAllServiceIds(booking.getServiceIds());
            }
            BookingResponse response = builder.build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error fetching booking: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
