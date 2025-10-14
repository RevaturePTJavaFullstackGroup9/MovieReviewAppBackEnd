package com.revature.movie_review_back_end.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="reviews")
public class Review {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long reviewId;
    
    /* 
    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName="user_id")
    private User user_id;
    */

    // This establishes the ManyToOne relationship, BUT it avoids actually storing or loading the movie entity into the review. All we need is the id.
    @ManyToOne(fetch=jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name="movie_id", referencedColumnName="movie_id", insertable = false, updatable = false)
    private Movie movie;

    // This field holds the foreign object's ID for direct manuipulation.
    @Column(name="movie_id")
    @NotNull
    private Long movieId;
    
    private String reviewText;

    private Boolean isLiked;

    private Boolean isHidden;
}
