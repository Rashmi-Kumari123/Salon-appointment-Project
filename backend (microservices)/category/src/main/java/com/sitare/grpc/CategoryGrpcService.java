package com.sitare.grpc;

import com.sitare.modal.Category;
import com.sitare.service.CategoryService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class CategoryGrpcService extends CategoryServiceGrpc.CategoryServiceImplBase {

    private final CategoryService categoryService;

    @Override
    public void getCategoryById(GetCategoryByIdRequest request, StreamObserver<CategoryResponse> responseObserver) {
        try {
            Long categoryId = request.getCategoryId();
            Category category = categoryService.getCategoryById(categoryId);
            
            if (category == null) {
                responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Category not found with id: " + categoryId)
                        .asRuntimeException());
                return;
            }
            
            CategoryResponse response = CategoryResponse.newBuilder()
                    .setId(category.getId())
                    .setName(category.getName() != null ? category.getName() : "")
                    .setImage(category.getImage() != null ? category.getImage() : "")
                    .setSalonId(category.getSalonId() != null ? category.getSalonId() : 0)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Error fetching category: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
