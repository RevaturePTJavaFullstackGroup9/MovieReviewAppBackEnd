package com.revature.movie_review_back_end.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="reviews", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "movie_id"})
})
public class Review {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long reviewId;
    
    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName="id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name="movie_id", referencedColumnName="movie_id", nullable = false)
    private Movie movie;

    private String reviewText;

    private Boolean isLiked;

    private Boolean isHidden;
}
