
// This interface defines the main actions you can do with movies in the app.
// It helps organize the code for finding, adding, updating, and deleting movies.
package com.revature.movie_review_back_end;

import java.util.List;

public interface MovieServiceInterface {

    // Get a list of all movies
    List<MovieModel> findAll();
    // Find a movie by its ID
    MovieModel findById(Long id);
    // Add a new movie
    MovieModel create(MovieModel movie);
    // Update an existing movie by its ID
    MovieModel update(Long id, MovieModel movie);
    // Delete a movie by its ID
    void delete(Long id);

    // Search for movies by part of their title
    List<MovieModel> searchByTitle(String query);
}