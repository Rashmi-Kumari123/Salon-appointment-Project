package com.sitare.service;

import com.sitare.modal.ServiceOffering;
import com.sitare.payload.dto.CategoryDTO;
import com.sitare.payload.dto.SalonDTO;
import com.sitare.payload.dto.ServiceDTO;

import java.util.Set;

public interface ServiceOfferingService {

    ServiceOffering createService(
            ServiceDTO service,
            SalonDTO salon,
            CategoryDTO category
    );
    ServiceOffering updateService(
            Long serviceId,
            ServiceOffering service
    ) throws Exception;

    Set<ServiceOffering> getAllServicesBySalonId(Long salonId,Long categoryId);

    ServiceOffering getServiceById(Long serviceId);

    Set<ServiceOffering> getServicesByIds(Set<Long> ids);
}
