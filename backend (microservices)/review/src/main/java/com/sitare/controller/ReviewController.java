package com.sitare.controller;


import com.sitare.exception.UserException;
import com.sitare.mapper.ReviewMapper;
import com.sitare.modal.Review;

import com.sitare.payload.dto.ReviewDTO;
import com.sitare.payload.dto.SalonDTO;
import com.sitare.payload.dto.UserDTO;
import com.sitare.payload.request.CreateReviewRequest;
import com.sitare.payload.response.ApiResponse;

import com.sitare.service.ReviewService;
import com.sitare.service.clients.SalonGrpcClient;
import com.sitare.service.clients.UserGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserGrpcClient userService;
    private final SalonGrpcClient salonService;

    @GetMapping("/salon/{salonId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(
            @PathVariable Long salonId) {

        List<Review> reviews = reviewService.getReviewsBySalonId(salonId);

        List<ReviewDTO> reviewDTOS =  reviews.stream().map((review)->
                {
                    UserDTO user= null;
                    try {
                        user = userService.getUserById(review.getUserId());
                    } catch (UserException e) {
                        throw new RuntimeException(e);
                    }
                    return ReviewMapper.mapToDTO(review,user);
                }
        ).toList();
        return ResponseEntity.ok(reviewDTOS);
    }

    @PostMapping("/salon/{salonId}")
    public ResponseEntity<ReviewDTO> writeReview(
            @RequestBody CreateReviewRequest req,
            @PathVariable Long salonId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        UserDTO user = userService.getUserFromJwtToken(jwt);
        SalonDTO product = salonService.getSalonById(salonId);


        Review review = reviewService.createReview(
                req, user, product
        );
        UserDTO reviewer = userService.getUserById(
                review.getUserId()
        );

        ReviewDTO reviewDTO= ReviewMapper.mapToDTO(review,reviewer);

        return ResponseEntity.ok(reviewDTO);
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(
            @RequestBody CreateReviewRequest req,
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        UserDTO user = userService.getUserFromJwtToken(jwt);

        Review review = reviewService.updateReview(
                reviewId,
                req.getReviewText(),
                req.getReviewRating(),
                user.getId()
        );
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwt) throws Exception
            {
        UserDTO user = userService.getUserFromJwtToken(jwt);

        reviewService.deleteReview(reviewId, user.getId());
        ApiResponse res = new ApiResponse("Review deleted successfully");
        return ResponseEntity.ok(res);
    }
}
