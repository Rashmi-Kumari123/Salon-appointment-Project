package com.sitare.service.clients;

import com.sitare.exception.UserException;
import com.sitare.grpc.*;
import com.sitare.payload.dto.SalonDTO;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SalonGrpcClient {

    @GrpcClient("salon-service")
    private SalonServiceGrpc.SalonServiceBlockingStub salonServiceStub;
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public SalonDTO getSalonByOwner(String jwt) throws UserException {
        try {
            GetSalonByOwnerRequest request = GetSalonByOwnerRequest.newBuilder()
                    .setJwtToken(jwt)
                    .build();
            
            SalonResponse response = salonServiceStub.getSalonByOwner(request);
            
            return mapToSalonDTO(response);
        } catch (Exception e) {
            throw new UserException("Error fetching salon from gRPC: " + e.getMessage());
        }
    }

    public SalonDTO getSalonById(Long salonId) throws UserException {
        try {
            GetSalonByIdRequest request = GetSalonByIdRequest.newBuilder()
                    .setSalonId(salonId)
                    .build();
            SalonResponse response = salonServiceStub.getSalonById(request);
            return mapToSalonDTO(response);
        } catch (Exception e) {
            throw new UserException("Error fetching salon from gRPC: " + e.getMessage());
        }
    }
    private SalonDTO mapToSalonDTO(SalonResponse response) {
        SalonDTO salonDTO = new SalonDTO();
        salonDTO.setId(response.getId());
        salonDTO.setName(response.getName());
        salonDTO.setAddress(response.getAddress());
        salonDTO.setPhoneNumber(response.getPhoneNumber());
        salonDTO.setEmail(response.getEmail());
        salonDTO.setCity(response.getCity());
        salonDTO.setOpen(response.getIsOpen());
        salonDTO.setHomeService(response.getHomeService());
        salonDTO.setActive(response.getActive());
        salonDTO.setOwnerId(response.getOwnerId());
        
        if (!response.getOpenTime().isEmpty()) {
            salonDTO.setOpenTime(LocalTime.parse(response.getOpenTime(), TIME_FORMATTER));
        }
        
        if (!response.getCloseTime().isEmpty()) {
            salonDTO.setCloseTime(LocalTime.parse(response.getCloseTime(), TIME_FORMATTER));
        }
        
        if (response.getImagesCount() > 0) {
            salonDTO.setImages(new ArrayList<>(response.getImagesList()));
        }
        return salonDTO;
    }
}
