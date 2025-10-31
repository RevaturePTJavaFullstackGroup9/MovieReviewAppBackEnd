package com.revature.movie_review_back_end.security.payloads;

import java.util.Set;

import com.revature.movie_review_back_end.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest {
    private String username;
    private String email;
    private String password;
    private User.Role role;
    
}
