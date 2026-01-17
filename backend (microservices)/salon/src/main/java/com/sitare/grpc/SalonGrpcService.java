package com.sitare.grpc;

import com.sitare.modal.Salon;
import com.sitare.service.SalonService;
import com.sitare.service.clients.UserGrpcClient;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class SalonGrpcService extends SalonServiceGrpc.SalonServiceImplBase {

    private final SalonService salonService;
    private final UserGrpcClient userGrpcClient;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void getSalonByOwner(GetSalonByOwnerRequest request, StreamObserver<SalonResponse> responseObserver) {
        try {
            String jwtToken = request.getJwtToken();
            com.sitare.payload.dto.UserDTO user = userGrpcClient.getUserFromJwtToken(jwtToken);
            
            Salon salon = salonService.getSalonByOwnerId(user.getId());
            
            if (salon == null) {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Salon not found for owner id: " + user.getId())
                        .asRuntimeException());
                return;
            }
            
            SalonResponse response = buildSalonResponse(salon);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error fetching salon: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getSalonById(GetSalonByIdRequest request, StreamObserver<SalonResponse> responseObserver) {
        try {
            Long salonId = request.getSalonId();
            Salon salon = salonService.getSalonById(salonId);
            
            if (salon == null) {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Salon not found with id: " + salonId)
                        .asRuntimeException());
                return;
            }
            
            SalonResponse response = buildSalonResponse(salon);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error fetching salon: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private SalonResponse buildSalonResponse(Salon salon) {
        SalonResponse.Builder builder = SalonResponse.newBuilder()
                .setId(salon.getId())
                .setName(salon.getName() != null ? salon.getName() : "")
                .setAddress(salon.getAddress() != null ? salon.getAddress() : "")
                .setPhoneNumber(salon.getPhoneNumber() != null ? salon.getPhoneNumber() : "")
                .setEmail(salon.getEmail() != null ? salon.getEmail() : "")
                .setCity(salon.getCity() != null ? salon.getCity() : "")
                .setIsOpen(salon.isOpen())
                .setHomeService(salon.isHomeService())
                .setActive(salon.isActive())
                .setOwnerId(salon.getOwnerId());

        if (salon.getOpenTime() != null) {
            builder.setOpenTime(salon.getOpenTime().format(TIME_FORMATTER));
        } else {
            builder.setOpenTime("");
        }

        if (salon.getCloseTime() != null) {
            builder.setCloseTime(salon.getCloseTime().format(TIME_FORMATTER));
        } else {
            builder.setCloseTime("");
        }

        if (salon.getImages() != null) {
            builder.addAllImages(salon.getImages());
        }
        return builder.build();
    }
}
