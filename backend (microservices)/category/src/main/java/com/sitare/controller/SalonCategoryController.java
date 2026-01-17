package com.sitare.controller;

import com.sitare.modal.Category;
import com.sitare.payload.dto.SalonDTO;
import com.sitare.service.CategoryService;
import com.sitare.service.clients.SalonGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories/salon-owner")
@RequiredArgsConstructor
public class SalonCategoryController {

    private final CategoryService categoryService;
    private final SalonGrpcClient salonService;
    @PostMapping
    public ResponseEntity<Category> createCategory(
            @RequestBody Category category,
            @RequestHeader("Authorization") String jwt) throws Exception {
        SalonDTO salon=salonService.getSalonByOwner(jwt);

        Category savedCategory = categoryService.saveCategory(category, salon);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }
}
