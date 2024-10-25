package com.eastern.clothy.user.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "AppUser")
@AllArgsConstructor
@Data
public class AppUser {
    public AppUser() {
        this.role = UserRole.USER;  // Setting the default value
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String email;
    @Column(nullable = false, unique = true)
    String username;
    String password;
    String fullName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    UserRole role;
    int cartId;

}
