package com.revature.movie_review_back_end.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.revature.movie_review_back_end.exception.ReviewAlreadyPostedException;
import com.revature.movie_review_back_end.exception.ReviewNotFoundException;
import com.revature.movie_review_back_end.model.Review;
import com.revature.movie_review_back_end.repo.MovieRepository;
import com.revature.movie_review_back_end.repo.ReviewRepository;
import com.revature.movie_review_back_end.repo.UserRepository;
import com.revature.movie_review_back_end.model.ReviewDTO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public ReviewDTO createReview(ReviewDTO reviewDTO) throws ReviewAlreadyPostedException{
        // While the SQL server has contraints to ensure duplicate reviews do not get uploaded,
        // its still good to catch that ourselves
        Optional<Review> existingReview = reviewRepository.findByMovieIdAndUserId(reviewDTO.getMovieId(), reviewDTO.getUserId());
        if (existingReview.isPresent()){
            throw new ReviewAlreadyPostedException("You have already created a review for this movie! If you wish to edit it, please send a PATCH request instead!");
        }
        Review review = ReviewDTO.convertFromDto(reviewDTO, movieRepository, userRepository);
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

    public double getAverageReviewScoreForMovie(Long movieId){
        return reviewRepository.findAverageReviewScoreByMovieId(movieId);
    }
    
}
