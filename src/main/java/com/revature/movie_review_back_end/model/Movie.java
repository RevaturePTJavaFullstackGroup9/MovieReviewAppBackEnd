// MovieModel represents a movie entity for the Movie Review App backend.
// It is mapped to the 'movies' table in the database and includes validation and persistence annotations.
//Author: Kevin Martinez 10/12/2025

package com.revature.movie_review_back_end.model;

import jakarta.persistence.*; // JPA annotations for ORM mapping
import jakarta.validation.constraints.*; // Bean validation annotations
import lombok.*; // Lombok for boilerplate code reduction

import java.math.BigDecimal; // For box office sales
import java.time.LocalDate; // For release date


@Entity // Marks this class as a JPA entity
@Table(name = "movies") // Maps to the 'movies' table
@Getter // Lombok: generates getters
@Setter // Lombok: generates getters
@NoArgsConstructor // Lombok: generates no-args constructor
@AllArgsConstructor // Lombok: generates all-args constructor
public class Movie {

    /**
     * Unique identifier for the movie (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "movie_id")
    private Long id;

    /**
     * Title of the movie. Cannot be blank.
     */
    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Release date of the movie. Must be in the past or present.
     */
    @PastOrPresent
    @Column(name = "release_date")
    private LocalDate releaseDate;

    /**
     * Genre of the movie (e.g., Action, Comedy).
     */
    @Column(length = 100)
    private String genre;

    /**
     * Director of the movie.
     */
    @Column(length = 150)
    private String director;

    /**
     * Name of the first lead actor.
     */
    @Column(name = "lead_actor_1", length = 150)
    private String leadActor1;

    /**
     * Name of the second lead actor.
     */
    @Column(name = "lead_actor_2", length = 150)
    private String leadActor2;

    /**
     * Box office sales in millions (e.g., 700.00 = $700,000,000).
     * Allows up to 9 digits and 2 decimal places.
     */
    @Digits(integer = 9, fraction = 2)
    @Column(name = "sales_millions", precision = 11, scale = 2)
    private BigDecimal salesMillions;

}
