package com.revature.movie_review_back_end.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.revature.movie_review_back_end.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByStatus(String status);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
