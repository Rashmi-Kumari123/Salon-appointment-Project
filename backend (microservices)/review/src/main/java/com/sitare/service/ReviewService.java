package com.sitare.service;

import com.sitare.modal.Review;
import com.sitare.payload.dto.SalonDTO;
import com.sitare.payload.dto.UserDTO;
import com.sitare.payload.request.CreateReviewRequest;


import javax.naming.AuthenticationException;
import java.util.List;

public interface ReviewService {

    Review createReview(CreateReviewRequest req,
                        UserDTO user,
                        SalonDTO salon);

    List<Review> getReviewsBySalonId(Long productId);

    Review updateReview(Long reviewId,
                        String reviewText,
                        double rating,
                        Long userId) throws Exception, AuthenticationException;

    void deleteReview(Long reviewId, Long userId) throws Exception, AuthenticationException;
}
