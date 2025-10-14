// This interface helps us work with movies in the database.
// It lets us save, find, update, and delete movies easily.

package com.revature.movie_review_back_end.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.movie_review_back_end.model.Movie;

import java.time.LocalDate;
import java.util.List;

// We extend JpaRepository so we get basic database methods for free.
public interface MovieRepository extends JpaRepository<Movie, Long> {

    //Custom query methods for common searches

    // Find movies with titles that contain a certain word (not case sensitive)
    List<Movie> findByTitleContainingIgnoreCase(String titlePart);

    // Find movies by director name (not case sensitive)
    List<Movie> findByDirectorIgnoreCase(String director);

    // Find movies by genre (not case sensitive)
    List<Movie> findByGenreIgnoreCase(String genre);

    // Find movies released between two dates
    List<Movie> findByReleaseDateBetween(LocalDate start, LocalDate end);

    // Get the top 10 movies with the highest sales
    List<Movie> findTop10ByOrderBySalesMillionsDesc();
}