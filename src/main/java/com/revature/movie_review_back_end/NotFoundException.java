package com.revature.movie_review_back_end;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}