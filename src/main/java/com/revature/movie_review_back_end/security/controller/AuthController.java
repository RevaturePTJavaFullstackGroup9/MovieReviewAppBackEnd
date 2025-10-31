package com.revature.movie_review_back_end.security.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.movie_review_back_end.model.User;
import com.revature.movie_review_back_end.repo.UserRepository;
import com.revature.movie_review_back_end.security.jwt.JwtUtils;
import com.revature.movie_review_back_end.security.payloads.JwtResponse;
import com.revature.movie_review_back_end.security.payloads.LoginRequest;
import com.revature.movie_review_back_end.security.payloads.MessageResponse;
import com.revature.movie_review_back_end.security.payloads.SignupRequest;
import com.revature.movie_review_back_end.security.service.UserDetailsImpl;
import com.revature.movie_review_back_end.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    //@Autowired
    //UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(new JwtResponse(
                    jwt, 
                    JwtResponse.Type.Bearer, 
                    userDetails.getId(), 
                    userDetails.getUsername(), 
                    userDetails.getEmail(),
                    roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Create new user's account
        User user = new User(null, 
            signUpRequest.getUsername(), 
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()),
            signUpRequest.getRole(),
            User.Status.ACTIVE);

        userService.createUser(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

}
