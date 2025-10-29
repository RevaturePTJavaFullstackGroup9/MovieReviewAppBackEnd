package com.revature.movie_review_back_end.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor // Needed for testing with JPA to avoid error? "HHH000143: Bytecode enhancement failed because no public, protected or package-private default constructor was found for entity: com.revature.movie_review_back_end.model.User. Private constructors don't work with runtime proxies"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;   // USER or ADMIN
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE; // ACTIVE or BANNED

    public enum Status{
        ACTIVE,
        BANNED,
    }

    public enum Role {
        USER,
        ADMIN
    }
}