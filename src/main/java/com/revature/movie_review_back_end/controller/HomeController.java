package com.revature.movie_review_back_end.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HomeController handles basic API endpoints for the Movie Review App.
 * Provides a simple endpoint to retrieve the application title.
 * Frontend will call GET /api/title to display the homepage title.
 * @author Kevin Antonio Martinez
 */

 
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" }) // Vite or CRA
public class HomeController {


    /**
     * GET /api/title
     * Returns a small JSON object with a single "title" field.
    */

    @GetMapping("/title")
    public Map<String, String> getTitle() {
        return Map.of("title", "Movie Review App");
    }
    
}
