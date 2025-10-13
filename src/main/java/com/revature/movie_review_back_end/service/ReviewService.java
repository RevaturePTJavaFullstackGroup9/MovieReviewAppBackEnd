package com.revature.movie_review_back_end.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.revature.movie_review_back_end.exception.ReviewNotFoundException;
import com.revature.movie_review_back_end.model.Review;
import com.revature.movie_review_back_end.repo.ReviewRepository;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review createReview(Review review){
        return reviewRepository.save(review);
    }

    public List<Review> getAllReviews(){
        return reviewRepository.findAll();
    }

    public Review updateReview(Long id, Review review) throws ReviewNotFoundException {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id " + id));

        existingReview.setReview_text(review.getReview_text());
        existingReview.setIs_liked(review.getIs_liked());
        existingReview.setIs_hidden(review.getIs_hidden());

        return reviewRepository.save(existingReview);
    }

    public Review getReviewById(Long id) throws ReviewNotFoundException {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id " + id));
    }

    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }
}
