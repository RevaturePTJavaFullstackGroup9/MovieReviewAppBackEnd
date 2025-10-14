package com.revature.movie_review_back_end.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDTO {
    private Long reviewId;
    private Long movieId;
    private String reviewText;
    private Boolean isLiked;
    private Boolean isHidden;

    public static ReviewDTO convertToDto(Review review) {
        return new ReviewDTO(
            review.getReviewId(),
            review.getMovieId(),
            review.getReviewText(),
            review.getIsLiked(),
            review.getIsHidden()
        );

    }
}
