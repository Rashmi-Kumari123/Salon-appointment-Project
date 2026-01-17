package com.sitare.service.clients;

import com.sitare.exception.UserException;
import com.sitare.grpc.*;
import com.sitare.payload.dto.ServiceOfferingDTO;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ServiceOfferingGrpcClient {
    @GrpcClient("service-offering")
    private ServiceOfferingServiceGrpc.ServiceOfferingServiceBlockingStub serviceOfferingServiceStub;

    public Set<ServiceOfferingDTO> getServicesByIds(Set<Long> serviceIds) throws UserException {
        try {
            GetServicesByIdsRequest.Builder requestBuilder = GetServicesByIdsRequest.newBuilder();
            requestBuilder.addAllServiceIds(serviceIds);
            GetServicesByIdsResponse response = serviceOfferingServiceStub.getServicesByIds(requestBuilder.build());
            Set<ServiceOfferingDTO> serviceOfferingDTOs = new HashSet<>();
            for (ServiceOfferingResponse serviceResponse : response.getServicesList()) {
                ServiceOfferingDTO serviceDTO = new ServiceOfferingDTO();
                serviceDTO.setId(serviceResponse.getId());
                serviceDTO.setName(serviceResponse.getName());
                serviceDTO.setDescription(serviceResponse.getDescription());
                serviceDTO.setPrice(serviceResponse.getPrice());
                serviceDTO.setDuration(serviceResponse.getDuration());
                serviceDTO.setSalon(serviceResponse.getSalonId());
                serviceDTO.setAvailable(serviceResponse.getAvailable());
                serviceDTO.setCategory(serviceResponse.getCategoryId());
                serviceDTO.setImage(serviceResponse.getImage());
                
                serviceOfferingDTOs.add(serviceDTO);
            }
            return serviceOfferingDTOs;
        } catch (Exception e) {
            throw new UserException("Error fetching services from gRPC: " + e.getMessage());
        }
    }
}
