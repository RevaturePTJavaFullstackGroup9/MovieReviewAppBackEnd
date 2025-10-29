package com.revature.movie_review_back_end.controller;

import com.revature.movie_review_back_end.model.Movie;
import com.revature.movie_review_back_end.service.MovieServiceInterface;

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
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class MovieController {

    private final MovieServiceInterface service;

    public MovieController(MovieServiceInterface service) {
        this.service = service;
    }

    // ---------- PUBLIC ----------
    // GET /api/movies
    @GetMapping("/movies")
    public List<Movie> all() {
        return service.findAll();
    }

    // GET /api/movies/{id}
    @GetMapping("/movies/{id}")
    public Movie one(@PathVariable Long id) {
        return service.findById(id);
    }

    // GET /api/movies/search?q=heat
    @GetMapping("/movies/search")
    public List<Movie> search(@RequestParam("q") String query) {
        return service.searchByTitle(query);
    }

    // ---------- ADMIN ----------
    // POST /api/admin/movies
    @PostMapping("/admin/movies")
    public ResponseEntity<Movie> create(@Valid @RequestBody Movie body) {
        Movie saved = service.create(body);
        return ResponseEntity.created(URI.create("/api/movies/" + saved.getId())).body(saved);
    }

    // PUT /api/admin/movies/{id}
    @PutMapping("/admin/movies/{id}")
    public Movie update(@PathVariable Long id, @Valid @RequestBody Movie body) {
        return service.update(id, body);
    }

    // DELETE /api/admin/movies/{id}
    @DeleteMapping("/admin/movies/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    
}
