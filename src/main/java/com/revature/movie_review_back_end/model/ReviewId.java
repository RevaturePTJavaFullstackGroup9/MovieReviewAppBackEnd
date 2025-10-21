package com.revature.movie_review_back_end.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReviewId implements Serializable{
    private Long reviewId;
    private Long userId;
    private Long movieId;
}
