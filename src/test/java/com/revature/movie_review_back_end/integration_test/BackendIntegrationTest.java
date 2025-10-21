package com.revature.movie_review_back_end.integration_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import com.revature.movie_review_back_end.controller.MovieController;
import com.revature.movie_review_back_end.controller.ReviewController;
import com.revature.movie_review_back_end.controller.UserController;
import com.revature.movie_review_back_end.exception.ExceptionController;
import com.revature.movie_review_back_end.model.*;
import com.revature.movie_review_back_end.repo.ReviewRepository;
import com.revature.movie_review_back_end.service.ReviewService;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Transactional // Doesn't actually work when using restTemplate, since restTemplate commits using its own transaction context.
public class BackendIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void tearDown() {
        /*
         * Very important! This will make sure that the tests don't interfere with each other!
         * Without this, since these all write to the H2 db, these tests would interfere with eachother.
         */
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "reviews", "movies", "users"); 
    }

    private ResponseEntity<User> postUser(){
        User user = new User(null, "test_user", "test_user@mail.com", "password", "USER", "ACTIVE");
        return restTemplate.postForEntity("/users", user, User.class);
    }

    private ResponseEntity<Movie> postMovie(){
        Movie movie = new Movie(null, "Movie 1", LocalDate.now(), "Action", "Director A", "Lead Actor 1", "Lead Actor 2", new BigDecimal(100));
        return restTemplate.postForEntity("/api/movies", movie, Movie.class);
    }

    private ResponseEntity<ReviewDTO> postReview(Long movieId, Long userId){
        ReviewDTO reviewDTO = new ReviewDTO(null, userId, movieId, "Liked", true, false);
        return restTemplate.postForEntity("/reviews", reviewDTO, ReviewDTO.class);
    }

    private <T> ResponseEntity<T> postReview(Long movieId, Long userId, Class<T> clazz){
        ReviewDTO reviewDTO = new ReviewDTO(null, userId, movieId, "Liked", true, false);
        return restTemplate.postForEntity("/reviews", reviewDTO, clazz);
    }

    @Test
    @Order(1)
    public void testCreateUser(){
        ResponseEntity<User> postUserResponse = this.postUser();
        Long userId = postUserResponse.getBody().getId();

        assertThat(postUserResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userId).isNotNull();        
    }

    @Test
    @Order(2)
    public void testCreateMovie(){
        ResponseEntity<Movie> postMovieResponse = this.postMovie();
        Long movieId = postMovieResponse.getBody().getId();

        assertThat(postMovieResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(movieId).isNotNull();
    }

    @Test
    @Order(3)
    public void testCreateReview(){
        Long userId = this.postUser().getBody().getId();
        Long movieId = this.postMovie().getBody().getId();
        
        ResponseEntity<ReviewDTO> postReviewResponse = postReview(movieId, userId);
        Long reviewId = postReviewResponse.getBody().getReviewId();

        assertThat(postReviewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reviewId).isNotNull();
    }

    @Test
    @Order(4) // So that this runs after the other ones, to prove the state of the DB gets cleaned after tests.
    public void testGetUsersOnEmptyDb(){
        // This test is here to prove the H2 database has not been contaminated by the other tests.
        List<User> users = restTemplate.getForEntity("/users", List.class).getBody();
        assertThat(users.size()).isZero();
    }

    @Test
    public void testNoDuplicateUsers(){
        this.postUser();
        this.postUser();

        List<User> users = restTemplate.getForEntity("/users", List.class).getBody();
        assertThat(users.size()).isOne();
    }

    @Test
    public void testUserCantLeaveTwoReviewsOnSameMovie(){
        Long userId = this.postUser().getBody().getId();
        Long movieId = this.postMovie().getBody().getId();
        
        this.postReview(movieId, userId);
        this.postReview(movieId, userId);

        List<Review> reviews = restTemplate.getForEntity("/reviews", List.class).getBody();
        assertThat(reviews.size()).isOne();
    }

    @Test
    public void testUserGetsErrorWhenLeavingTwoReviewsOnSameMovie(){
        Long userId = this.postUser().getBody().getId();
        Long movieId = this.postMovie().getBody().getId();

        this.postReview(movieId, userId);
        ResponseEntity<ExceptionController.ErrorResponse> response = this.postReview(movieId, userId, ExceptionController.ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isNotBlank();
    }
}
