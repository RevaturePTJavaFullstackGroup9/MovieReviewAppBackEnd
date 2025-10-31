package com.revature.movie_review_back_end.security.payloads;

import java.util.List;

import com.revature.movie_review_back_end.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private Type type; // "Bearer"
    private Long id;
    private String username;
    private String email;
    private List<String> role;

    public enum Type{
        Bearer,
    }
    
}
