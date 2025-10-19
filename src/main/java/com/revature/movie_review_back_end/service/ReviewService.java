package com.revature.movie_review_back_end.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.revature.movie_review_back_end.exception.ReviewNotFoundException;
import com.revature.movie_review_back_end.model.Movie;
import com.revature.movie_review_back_end.model.Review;
import com.revature.movie_review_back_end.repo.MovieRepository;
import com.revature.movie_review_back_end.repo.ReviewRepository;
import com.revature.movie_review_back_end.model.ReviewDTO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    
    public ReviewDTO createReview(ReviewDTO reviewDTO){
        Review review = ReviewDTO.convertFromDto(reviewDTO, movieRepository);
        return ReviewDTO.convertToDto(reviewRepository.save(review));
    }

    public List<ReviewDTO> getAllReviews(){
        List <Review> reviews = reviewRepository.findAll();
        List <ReviewDTO> reviewDTOs = reviews.stream().map(ReviewDTO::convertToDto).toList();
        return reviewDTOs;
    }

    public List<ReviewDTO> getReviewsByMovieId(Long movieId) {
        List <Review> reviews = reviewRepository.findAllByMovieId(movieId);
        List <ReviewDTO> reviewDTOs = reviews.stream().map(ReviewDTO::convertToDto).toList();
        return reviewDTOs;
    }

    public ReviewDTO updateReview(Long id, Review review) throws ReviewNotFoundException {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id " + id));

        existingReview.setReviewText(review.getReviewText());
        existingReview.setIsLiked(review.getIsLiked());
        existingReview.setIsHidden(review.getIsHidden());

        return ReviewDTO.convertToDto(reviewRepository.save(existingReview));
    }

    public ReviewDTO getReviewById(Long id) throws ReviewNotFoundException {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id " + id));
        return ReviewDTO.convertToDto(review);
    }

    public void deleteReviewById(Long id) {
        reviewRepository.deleteById(id);
    }
}
