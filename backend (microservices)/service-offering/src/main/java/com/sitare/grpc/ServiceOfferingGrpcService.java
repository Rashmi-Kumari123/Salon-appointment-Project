package com.sitare.grpc;

import com.sitare.modal.ServiceOffering;
import com.sitare.service.ServiceOfferingService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Set;

@GrpcService
@RequiredArgsConstructor
public class ServiceOfferingGrpcService extends ServiceOfferingServiceGrpc.ServiceOfferingServiceImplBase {

    private final ServiceOfferingService serviceOfferingService;

    @Override
    public void getServicesByIds(GetServicesByIdsRequest request, StreamObserver<GetServicesByIdsResponse> responseObserver) {
        try {
            Set<Long> serviceIds = new java.util.HashSet<>(request.getServiceIdsList());
            Set<ServiceOffering> services = serviceOfferingService.getServicesByIds(serviceIds);
            
            GetServicesByIdsResponse.Builder responseBuilder = GetServicesByIdsResponse.newBuilder();
            
            for (ServiceOffering service : services) {
                ServiceOfferingResponse serviceResponse = ServiceOfferingResponse.newBuilder()
                        .setId(service.getId())
                        .setName(service.getName() != null ? service.getName() : "")
                        .setDescription(service.getDescription() != null ? service.getDescription() : "")
                        .setPrice(service.getPrice())
                        .setDuration(service.getDuration())
                        .setSalonId(service.getSalonId() != null ? service.getSalonId() : 0)
                        .setAvailable(service.isAvailable())
                        .setCategoryId(service.getCategoryId() != null ? service.getCategoryId() : 0)
                        .setImage(service.getImage() != null ? service.getImage() : "")
                        .build();
                
                responseBuilder.addServices(serviceResponse);
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error fetching services: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
