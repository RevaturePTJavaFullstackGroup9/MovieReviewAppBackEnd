// Service class that handles business logic for movies.
// It connects the controller to the repository and manages CRUD operations.

package com.revature.movie_review_back_end;
import com.revature.movie_review_back_end.MovieModel;
import com.revature.movie_review_back_end.MovieRepository;
import com.revature.movie_review_back_end.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import lombok.*;

import java.util.List;


@Service // Marks this class as a Spring service
@Transactional // Ensures database operations are done in a transaction
public class MovieServiceImpl implements MovieServiceInterface {

    // The repository used to access movie data in the database
    private final MovieRepository repo;

    // Constructor injection for the repository
    public MovieServiceImpl(MovieRepository repo) {
        this.repo = repo;
    }

    // Get all movies from the database
    @Override
    public List<MovieModel> findAll() {
        return repo.findAll();
    }

    // Find a movie by its ID, or throw an error if not found
    @Override
    public MovieModel findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Movie not found: id=" + id));
    }

    // Add a new movie to the database
   
    @Override
    public MovieModel create(MovieModel movie) {
        movie.setId(null); // ensure insert
        return repo.save(movie);
    }

    // Update an existing movie's details
    @Override
    public MovieModel update(Long id, MovieModel incoming) {
        MovieModel existing = findById(id);
        // Copy all fields from the incoming movie to the existing one
        existing.setTitle(incoming.getTitle());
        existing.setReleaseDate(incoming.getReleaseDate());
        existing.setGenre(incoming.getGenre());
        existing.setDirector(incoming.getDirector());
        existing.setLeadActor1(incoming.getLeadActor1());
        existing.setLeadActor2(incoming.getLeadActor2());
        existing.setSalesMillions(incoming.getSalesMillions());
        return repo.save(existing);
    }

    // Delete a movie by its ID
    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Movie not found: id=" + id);
        }
        repo.deleteById(id);
    }

    // Search for movies by part of their title
    @Override
    public List<MovieModel> searchByTitle(String query) {
        return repo.findByTitleContainingIgnoreCase(query);
    }
}