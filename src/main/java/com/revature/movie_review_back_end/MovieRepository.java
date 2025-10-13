// This interface helps us work with movies in the database.
// It lets us save, find, update, and delete movies easily.

package com.revature.movie_review_back_end;

import com.revature.movie_review_back_end.MovieModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

// We extend JpaRepository so we get basic database methods for free.
public interface MovieRepository extends JpaRepository<MovieModel, Long> {

    //Custom query methods for common searches

    // Find movies with titles that contain a certain word (not case sensitive)
    List<MovieModel> findByTitleContainingIgnoreCase(String titlePart);

    // Find movies by director name (not case sensitive)
    List<MovieModel> findByDirectorIgnoreCase(String director);

    // Find movies by genre (not case sensitive)
    List<MovieModel> findByGenreIgnoreCase(String genre);

    // Find movies released between two dates
    List<MovieModel> findByReleaseDateBetween(LocalDate start, LocalDate end);

    // Get the top 10 movies with the highest sales
    List<MovieModel> findTop10ByOrderBySalesMillionsDesc();
}