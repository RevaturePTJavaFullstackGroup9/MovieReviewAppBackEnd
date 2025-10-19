package com.revature.movie_review_back_end.model;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.movie_review_back_end.repo.MovieRepository;
import com.revature.movie_review_back_end.repo.ReviewRepository;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDTO {
    // DTO = Data Transfer Object
    private Long reviewId;
    private Long movieId;
    private String reviewText;
    private Boolean isLiked;
    private Boolean isHidden;

    public static ReviewDTO convertToDto(Review review) {
        Long movieId = review.getMovie().getId();
        return new ReviewDTO(
            review.getReviewId(),
            movieId,
            review.getReviewText(),
            review.getIsLiked(),
            review.getIsHidden()
        );
    }

    public static Review convertFromDto(ReviewDTO reviewDTO, MovieRepository movieRepository){
        Review review = new Review();
        review.setReviewId(reviewDTO.getReviewId());
        review.setMovie(movieRepository.getReferenceById(reviewDTO.getMovieId()));
        review.setReviewText(reviewDTO.getReviewText());
        review.setIsLiked(reviewDTO.getIsLiked());
        review.setIsHidden(reviewDTO.getIsHidden());
        return review;
    }
}
