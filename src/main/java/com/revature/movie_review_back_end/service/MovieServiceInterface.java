
// This interface defines the main actions you can do with movies in the app.
// It helps organize the code for finding, adding, updating, and deleting movies.
package com.revature.movie_review_back_end.service;

import java.util.List;

import com.revature.movie_review_back_end.model.Movie;

public interface MovieServiceInterface {

    // Get a list of all movies
    List<Movie> findAll();
    // Find a movie by its ID
    Movie findById(Long id);
    // Add a new movie
    Movie create(Movie movie);
    // Update an existing movie by its ID
    Movie update(Long id, Movie movie);
    // Delete a movie by its ID
    void delete(Long id);

    // Search for movies by part of their title
    List<Movie> searchByTitle(String query);
}