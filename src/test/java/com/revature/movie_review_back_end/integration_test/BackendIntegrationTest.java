package com.revature.movie_review_back_end.integration_test;

import org.aspectj.bridge.Message;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.core.ParameterizedTypeReference;

import com.revature.movie_review_back_end.exception.ExceptionController;
import com.revature.movie_review_back_end.exception.ReviewAlreadyPostedException;
import com.revature.movie_review_back_end.model.*;
import com.revature.movie_review_back_end.repo.MovieRepository;
import com.revature.movie_review_back_end.repo.ReviewRepository;
import com.revature.movie_review_back_end.security.payloads.JwtResponse;
import com.revature.movie_review_back_end.security.payloads.LoginRequest;
import com.revature.movie_review_back_end.security.payloads.MessageResponse;
import com.revature.movie_review_back_end.security.payloads.SignupRequest;
import com.revature.movie_review_back_end.service.MovieServiceImpl;
import com.revature.movie_review_back_end.service.ReviewService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.net.http.HttpRequest;
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

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired 
    private ReviewService reviewService;

    @BeforeEach
    void tearDown() {
        /*
         * Very important! This will make sure that the tests don't interfere with each other!
         * Without this, since these all write to the H2 db, these tests would interfere with eachother.
         */
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "reviews", "movies", "users"); 
    }

    // Parameterized Type References for type safety. 
    // camelCase as its an instance of a class technically, but _t to distinguish it from standard variables
    ParameterizedTypeReference<List<User>> listUser_t = new ParameterizedTypeReference<List<User>>() {};
    ParameterizedTypeReference<List<Movie>> listMovie_t = new ParameterizedTypeReference<List<Movie>>() {};
    ParameterizedTypeReference<List<Review>> listReview_t = new ParameterizedTypeReference<List<Review>>() {};

    private ResponseEntity<User> postUser(){
        User user = new User(null, "test_user", "test_user@mail.com", "password", User.Role.USER, User.Status.ACTIVE);
        return restTemplate.postForEntity("/users", user, User.class);
    }

    private ResponseEntity<User> postUser(String username, String email){
        User user = new User(null, username, email, "password", User.Role.USER, User.Status.ACTIVE);
        return restTemplate.postForEntity("/users", user, User.class);
    }

    private ResponseEntity<Movie> postMovie(){
        Movie movie = new Movie(null, "Movie 1", LocalDate.now(), "Action", "Director A", "Lead Actor 1", "Lead Actor 2", new BigDecimal(100), "www.posterUrl.com", "This movie involved people.");
        return restTemplate.postForEntity("/api/admin/movies", movie, Movie.class);
    }

    private ResponseEntity<ReviewDTO> postReview(Long movieId, Long userId, Integer reviewScore){
        ReviewDTO reviewDTO = new ReviewDTO(null, userId, movieId, "Liked", false, "A great movie ", reviewScore);
        return restTemplate.postForEntity("/reviews", reviewDTO, ReviewDTO.class);
    }

    private ResponseEntity<ReviewDTO> postReview(Long movieId, Long userId){
        ReviewDTO reviewDTO = new ReviewDTO(null, userId, movieId, "Liked", false, "A great movie ", 8);
        return restTemplate.postForEntity("/reviews", reviewDTO, ReviewDTO.class);
    }

    private <T> ResponseEntity<T> postReview(Long movieId, Long userId, Class<T> clazz){
        ReviewDTO reviewDTO = new ReviewDTO(null, userId, movieId, "Liked", false, "A great movie ", 8);
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
        userSignup();
        JwtResponse jwtResponse = userLogin().getBody();
        String token = jwtResponse.getToken();
        Long userId = jwtResponse.getId();

        Long movieId = this.postMovie().getBody().getId();
        
        ResponseEntity<ReviewDTO> postReviewResponse = postAuthenticatedReview(movieId, userId, token, ReviewDTO.class);
        Long reviewId = postReviewResponse.getBody().getReviewId();

        assertThat(postReviewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reviewId).isNotNull();
    }

    @Test
    @Order(4) // So that this runs after the other ones, to prove the state of the DB gets cleaned after tests.
    public void testGetUsersOnEmptyDb(){
        // This test is here to prove the H2 database has not been contaminated by the other tests.
        //List<User> users = restTemplate.getForEntity("/users", List.class).getBody();
        List<User> users = restTemplate.exchange("/users", HttpMethod.GET, null, listUser_t).getBody();
        assertThat(users.size()).isZero();
    }

    @Test
    public void testNoDuplicateUsers(){
        this.postUser();
        try{
            this.postUser();
        }
        catch(Exception e){
            // We don't really care in this test what exception gets thrown if at all
            // We just want to make sure only one user got posted
        }

        //List<User> users = restTemplate.getForEntity("/users", List.class).getBody();
        List<User> users = restTemplate.exchange("/users", HttpMethod.GET, null, listUser_t).getBody();
        assertThat(users.size()).isOne();
    }

    @Test
    public void testUserUsernameUniqueConstraint(){
        this.postUser("username", "a@mail.com");
        try { this.postUser("username", "b@mail.com"); } catch (Exception e) {}
        
        //List<User> users = restTemplate.getForEntity("/users", List.class).getBody();
        List<User> users = restTemplate.exchange("/users", HttpMethod.GET, null, listUser_t).getBody();
        assertThat(users.size()).isOne();
    }

    @Test
    public void testUserEmailUniqueConstraint(){
        this.postUser("username", "a@mail.com");
        try { this.postUser("other_username", "a@mail.com"); } catch (Exception e) {}
        
        //List<User> users = restTemplate.getForEntity("/users", List.class).getBody();
        List<User> users = restTemplate.exchange("/users", HttpMethod.GET, null, listUser_t).getBody();
        assertThat(users.size()).isOne();
    }



    @Test
    public void testUserCantLeaveTwoReviewsOnSameMovie(){
        userSignup();
        JwtResponse jwtResponse = userLogin().getBody();
        String token = jwtResponse.getToken();
        Long userId = jwtResponse.getId();
        Long movieId = this.postMovie().getBody().getId();
        
        this.postAuthenticatedReview(movieId, userId, token, ReviewDTO.class);
        this.postAuthenticatedReview(movieId, userId, token, ExceptionController.ErrorResponse.class);

        //List<Review> reviews = restTemplate.getForEntity("/reviews", List.class).getBody();
        List<Review> reviews = restTemplate.exchange("/reviews", HttpMethod.GET, null, listReview_t).getBody();
        assertThat(reviews.size()).isOne();
    }

    @Test
    public void testUserGetsErrorWhenLeavingTwoReviewsOnSameMovie(){
        userSignup();
        JwtResponse jwtResponse = userLogin().getBody();
        String token = jwtResponse.getToken();
        Long userId = jwtResponse.getId();

        Long movieId = this.postMovie().getBody().getId();

        this.postAuthenticatedReview(movieId, userId, token, ReviewDTO.class);
        ResponseEntity<ExceptionController.ErrorResponse> response = this.postAuthenticatedReview(movieId, userId, token, ExceptionController.ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isNotBlank();
    }



    // Auth Tests
    private ResponseEntity<MessageResponse> userSignup(String username, String email, String password, User.Role role){
        SignupRequest signupRequest = new SignupRequest(username, email, password, role);
        HttpEntity<SignupRequest> httpEntity = new HttpEntity<>(signupRequest);
        return restTemplate.exchange("/api/auth/signup", HttpMethod.POST, httpEntity, MessageResponse.class);
    }

    private ResponseEntity<MessageResponse> userSignup(){
        return userSignup("test", "test@mail.com", "password", User.Role.USER);
    }

    private ResponseEntity<JwtResponse> userLogin(){
        return userLogin("test", "password");
    }

    private ResponseEntity<JwtResponse> userLogin(String username, String password){
        LoginRequest loginRequest = new LoginRequest(username, password);
        HttpEntity<LoginRequest> httpEntityLoginRequest = new HttpEntity<>(loginRequest);
        return restTemplate.exchange("/api/auth/login", HttpMethod.POST, httpEntityLoginRequest, JwtResponse.class);
    }

    private <T> ResponseEntity<T> postAuthenticatedReview(ReviewDTO reviewDTO, String token, Class<T> clazz){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<ReviewDTO> postReviewHttpEntity = new HttpEntity<>(reviewDTO, headers);
        return restTemplate.exchange("/reviews", HttpMethod.POST, postReviewHttpEntity, clazz);
    }

    private <T> ResponseEntity<T> postAuthenticatedReview(Long movieId, Long userId, String token, Class<T> clazz){
        ReviewDTO reviewDTO = new ReviewDTO(null, userId, movieId, "Ok", false, "Title", 10);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<ReviewDTO> postReviewHttpEntity = new HttpEntity<>(reviewDTO, headers);
        return restTemplate.exchange("/reviews", HttpMethod.POST, postReviewHttpEntity, clazz);
    }

    @Test
    public void testUserSignupAndLoginWithJwt(){
        // Test Sign Up
        String username = "User";
        String email = "auth1@mail.com";
        String password = "password";
        SignupRequest signupRequest = new SignupRequest(username, email, password, User.Role.USER);
        HttpEntity<SignupRequest> httpEntity = new HttpEntity<SignupRequest>(signupRequest);
        ResponseEntity<MessageResponse> response = restTemplate.exchange("/api/auth/signup", HttpMethod.POST, httpEntity, MessageResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Test Login
        LoginRequest loginRequest = new LoginRequest(username, password);
        HttpEntity<LoginRequest> httpEntityLoginRequest = new HttpEntity<LoginRequest>(loginRequest);
        ResponseEntity<JwtResponse> loginResponse = restTemplate.exchange("/api/auth/login", HttpMethod.POST, httpEntityLoginRequest, JwtResponse.class);
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        String token = loginResponse.getBody().getToken();
        Long userId = loginResponse.getBody().getId();
        
        // Post a movie so we can post review
        Long movieId = this.postMovie().getBody().getId();

        // Test Posting Review As User
        ReviewDTO reviewDTO = new ReviewDTO(null, userId, movieId, "This movie is awesome", false, "Movie title", 10);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<ReviewDTO> postReviewHttpEntity = new HttpEntity<>(reviewDTO, headers);
        ResponseEntity<ReviewDTO> postReviewResponse = restTemplate.exchange("/reviews", HttpMethod.POST, postReviewHttpEntity, ReviewDTO.class);
        
        assertThat(postReviewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Review> reviews = restTemplate.exchange("/reviews", HttpMethod.GET, null, listReview_t).getBody();
        assertThat(reviews.size()).isOne();
        
    }

    @Test
    public void testUnauthorizedUserCantLeaveReview() {
        Long userId = this.postUser().getBody().getId();
        Long movieId = this.postMovie().getBody().getId();

        ResponseEntity<ExceptionController.ErrorResponse> response = this.postReview(movieId, userId, ExceptionController.ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testUserCantLeaveAReviewForAnotherUser() {
        userSignup("userA", "a@mail.com", "password", User.Role.USER);
        Long userAId = userLogin("userA", "password").getBody().getId();
        
        userSignup("userB", "b@mail.com", "password", User.Role.USER);
        ResponseEntity<JwtResponse> loginResponse = userLogin("userB", "password");
        String token = loginResponse.getBody().getToken();
        Long userBId = loginResponse.getBody().getId();

        Long movieId = this.postMovie().getBody().getId();

        ReviewDTO reviewDTO = new ReviewDTO(null, userAId, movieId, "Ok", false, "Title", 10);
        ResponseEntity<ExceptionController.ErrorResponse> postMovieResponse = this.postAuthenticatedReview(reviewDTO, token, ExceptionController.ErrorResponse.class);
        assertThat(postMovieResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        List<Review> reviews = restTemplate.exchange("/reviews", HttpMethod.GET, null, listReview_t).getBody();
        assertThat(reviews.size()).isZero();

    }
    
    // TODO: Test getting average score
    @Test
    public void testAverageReviewScoreForMovie(){
        Long userIdA = this.postUser("a", "a@mail.com").getBody().getId();
        Long userIdB = this.postUser("b", "b@mail.com").getBody().getId();
        Long movieId = this.postMovie().getBody().getId();

        assertDoesNotThrow(() -> reviewService.createReview(new ReviewDTO(null, userIdA, movieId, "reviewTest", false, "reviewTitle", 10)));
        assertDoesNotThrow(() -> reviewService.createReview(new ReviewDTO(null, userIdB, movieId, "reviewTest", false, "reviewTitle", 2)));
        
        Double averageReviewScore = reviewRepository.findAverageReviewScoreByMovieId(movieId);

        assertThat(averageReviewScore).isEqualTo(6.0);
        assertThat(reviewService.getAverageReviewScoreForMovie(movieId)).isEqualTo(6.0);
    }

    @Test
    public void testAverageReviewScoreReturnsZeroForMovieWithNoReviews(){
        Long movieId = this.postMovie().getBody().getId();
        Double reviewScore;

        reviewScore = assertDoesNotThrow(() -> reviewService.getAverageReviewScoreForMovie(movieId));
        assertThat(reviewScore).isEqualTo(0.0);
        
    }
    // TODO: Test for patching a review
    // TODO: Test user can't register with blank username / email / password 
    // TODO: Provide a 4XX Error for registering a duplicate user.
}
