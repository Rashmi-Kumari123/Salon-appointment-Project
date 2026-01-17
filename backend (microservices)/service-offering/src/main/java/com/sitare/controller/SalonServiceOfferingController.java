package com.sitare.controller;

import com.sitare.modal.ServiceOffering;
import com.sitare.payload.dto.CategoryDTO;
import com.sitare.payload.dto.SalonDTO;
import com.sitare.payload.dto.ServiceDTO;
import com.sitare.service.ServiceOfferingService;
import com.sitare.service.clients.CategoryGrpcClient;
import com.sitare.service.clients.SalonGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service-offering/salon-owner")
public class SalonServiceOfferingController {

    private final ServiceOfferingService serviceOfferingService;
    private final SalonGrpcClient salonService;
    private final CategoryGrpcClient categoryService;

    @PostMapping
    public ResponseEntity<ServiceOffering> createService(
            @RequestHeader("Authorization") String jwt,
            @RequestBody ServiceDTO service) throws Exception {

        SalonDTO salon=salonService.getSalonByOwner(jwt);

        CategoryDTO category=categoryService
                .getCategoryById(service.getCategory());

        ServiceOffering createdService = serviceOfferingService
                .createService(service,salon,category);
        return new ResponseEntity<>(createdService, HttpStatus.CREATED);
    }

    @PatchMapping("/{serviceId}")
    public ResponseEntity<ServiceOffering> updateService(
            @PathVariable Long serviceId,
            @RequestBody ServiceOffering service) throws Exception {
        ServiceOffering updatedService = serviceOfferingService
                .updateService(serviceId, service);
        if (updatedService != null) {
            return new ResponseEntity<>(updatedService, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
