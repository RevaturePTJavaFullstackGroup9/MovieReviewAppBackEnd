package com.revature.movie_review_back_end.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    private Long review_id;
    
    /* 
    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName="user_id")
    private User user_id;

    @ManyToOne
    @JoinColumn(name="movie_id", referencedColumnName="movie_id")
    private Movie movie_id;
    */

    private String review_text;

    private Boolean is_liked;

    private Boolean is_hidden;
}
