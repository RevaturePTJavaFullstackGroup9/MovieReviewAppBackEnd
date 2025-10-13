package com.revature.movie_review_back_end;

import com.revature.movie_review_back_end.MovieModel;
import com.revature.movie_review_back_end.MovieServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST controller for managing movies in the Movie Review App.
 * Provides endpoints for CRUD operations and searching movies.
 *   GET /api/movies</b>: Retrieve all movies.
 *   GET /api/movies/{id}</b>: Retrieve a movie by its ID.
 *   GET /api/movies/search?q=title</b>: Search movies by title.
 *   POST /api/movies</b>: Create a new movie.
 *  PUT /api/movies/{id}</b>: Update an existing movie.
 *   DELETE /api/movies/{id}</b>: Delete a movie by its ID.
 * Uses {@link MovieServiceInterface} for business logic.
 * @author Kevin Martinez
 */
@RestController
@RequestMapping("/api/movies")

public class MovieController {

    private final MovieServiceInterface service;

    public MovieController(MovieServiceInterface service) {
        this.service = service;
    }

    // GET /api/movies
    @GetMapping
    public List<MovieModel> all() {
        return service.findAll();
    }

    // GET /api/movies/{id}
    @GetMapping("/{id}")
    public MovieModel one(@PathVariable Long id) {
        return service.findById(id);
    }

    // GET /api/movies/search?q=heat
    @GetMapping("/search")
    public List<MovieModel> search(@RequestParam("q") String query) {
        return service.searchByTitle(query);
    }

    // POST /api/movies
    @PostMapping
    public ResponseEntity<MovieModel> create(@Valid @RequestBody MovieModel body) {
        MovieModel saved = service.create(body);
        return ResponseEntity
                .created(URI.create("/api/movies/" + saved.getId()))
                .body(saved);
    }

    // PUT /api/movies/{id}
    @PutMapping("/{id}")
    public MovieModel update(@PathVariable Long id, @Valid @RequestBody MovieModel body) {
        return service.update(id, body);
    }

    // DELETE /api/movies/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    
}
