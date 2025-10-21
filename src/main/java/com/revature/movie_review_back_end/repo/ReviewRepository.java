package com.revature.movie_review_back_end.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.revature.movie_review_back_end.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{
    List<Review> findAllByMovieId(Long movieId);
    Optional<Review> findByMovieIdAndUserId(Long movieId, Long userId);
}
