package com.sitare.service.clients;

import com.sitare.exception.UserException;
import com.sitare.grpc.*;
import com.sitare.payload.dto.CategoryDTO;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryGrpcClient {

    @GrpcClient("category-service")
    private CategoryServiceGrpc.CategoryServiceBlockingStub categoryServiceStub;

    public CategoryDTO getCategoryById(Long categoryId) throws UserException {
        try {
            GetCategoryByIdRequest request = GetCategoryByIdRequest.newBuilder()
                    .setCategoryId(categoryId)
                    .build();
            
            CategoryResponse response = categoryServiceStub.getCategoryById(request);
            
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setId(response.getId());
            categoryDTO.setName(response.getName());
            categoryDTO.setImage(response.getImage());
            
            return categoryDTO;
        } catch (Exception e) {
            throw new UserException("Error fetching category from gRPC: " + e.getMessage());
        }
    }
}
