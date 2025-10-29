package com.revature.movie_review_back_end.model;

import com.revature.movie_review_back_end.repo.MovieRepository;
import com.revature.movie_review_back_end.repo.UserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    // DTO = Data Transfer Object
    private Long reviewId;
    private Long userId;
    private Long movieId;
    private String reviewText;
    private Boolean isHidden;
    private String reviewTitle;
    private Integer reviewScore;

    public static ReviewDTO convertToDto(Review review) {
        return new ReviewDTO(
            review.getReviewId(),
            review.getUser().getId(),
            review.getMovie().getId(),
            review.getReviewText(),
            review.getIsHidden(),
            review.getReviewTitle(),
            review.getReviewScore()
        );
    }

    public static Review convertFromDto(ReviewDTO reviewDTO, MovieRepository movieRepository, UserRepository userRepository){
        return new Review(
            reviewDTO.getReviewId(),
            userRepository.getReferenceById(reviewDTO.getUserId()),
            movieRepository.getReferenceById(reviewDTO.getMovieId()),
            reviewDTO.getReviewText(),
            reviewDTO.getIsHidden(),
            reviewDTO.getReviewTitle(),
            reviewDTO.getReviewScore()
        );
    }
}
