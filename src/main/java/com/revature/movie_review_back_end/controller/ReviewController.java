package com.revature.movie_review_back_end.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.revature.movie_review_back_end.exception.ReviewNotFoundException;
import com.revature.movie_review_back_end.model.Review;
import com.revature.movie_review_back_end.model.ReviewDTO;
import com.revature.movie_review_back_end.service.ReviewService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @PostMapping("/reviews")
    public Review createReview(@RequestBody Review review) {
        Review createdReview = reviewService.createReview(review);
        return createdReview;
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReview(@PathVariable Long id){
        // Implement delete functionality if needed
        reviewService.deleteReviewById(id);
    }

    @PatchMapping("/reviews/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review review) throws ReviewNotFoundException{
        return reviewService.updateReview(id, review);
    }

    @GetMapping("/reviews/{id}")
    public Review getReviewById(@PathVariable Long id) throws ReviewNotFoundException {
        // Implement get by ID functionality if needed
        return reviewService.getReviewById(id);
    }
    
    // TODO: Get reviews by user ID, and by Movie ID;
    @GetMapping("/movies/{movieId}/reviews")
    public List<ReviewDTO> getReviewsByMovieId(@PathVariable Long movieId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByMovieId(movieId);
        return reviews;
    }
    
}
