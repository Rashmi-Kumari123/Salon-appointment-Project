package com.sitare.controller;

import com.sitare.modal.Category;
import com.sitare.payload.dto.SalonDTO;
import com.sitare.payload.dto.UserDTO;
import com.sitare.service.CategoryService;
import com.sitare.service.clients.SalonGrpcClient;
import com.sitare.service.clients.UserGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserGrpcClient userService;
    private final SalonGrpcClient salonService;

    // Get all Categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Get all Categories by Salon ID
    @GetMapping("/salon/{id}")
    public ResponseEntity<Set<Category>> getCategoriesBySalon(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {
        UserDTO user=userService.getUserFromJwtToken(jwt);
        SalonDTO salon=salonService.getSalonById(id);

        Set<Category> categories = categoryService
                .getAllCategoriesBySalon(salon.getId());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Get a Category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @RequestBody Category category
    ) throws Exception {
            Category updatedCategory = categoryService.updateCategory(id,category);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // Delete a Category by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
