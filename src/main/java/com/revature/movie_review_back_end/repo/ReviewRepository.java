package com.revature.movie_review_back_end.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.revature.movie_review_back_end.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{
    List<Review> findAllByMovieId(Long movieId);
    Optional<Review> findByMovieIdAndUserId(Long movieId, Long userId);
    @Query("SELECT AVG(CASE WHEN r.isLiked = TRUE THEN 1.0 ELSE 0.0 END) FROM Review r JOIN r.movie m WHERE m.id = :movieId")
    Double findAverageReviewScoreByMovieId(@Param("movieId") Long movieId);
}
