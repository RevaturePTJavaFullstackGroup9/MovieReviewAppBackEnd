package com.revature.movie_review_back_end.exception;

public class ReviewAlreadyPostedException extends Exception {
    public ReviewAlreadyPostedException(String message){
        super(message);
    }
}
