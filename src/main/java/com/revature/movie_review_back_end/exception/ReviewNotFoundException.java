package com.revature.movie_review_back_end.exception;

public class ReviewNotFoundException extends Exception {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
