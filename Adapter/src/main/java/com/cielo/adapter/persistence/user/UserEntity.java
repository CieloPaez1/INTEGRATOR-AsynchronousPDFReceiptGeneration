package com.cielo.adapter.persistence.user;

import enums.UserStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private String activationCode;

    private LocalDateTime activationExpiresAt;

    private LocalDateTime createdAt;

    public UserEntity() {
    }
    public UserEntity(Long id, String email, String password, UserStatus status, String activationCode, LocalDateTime activationExpiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.status = status;
        this.activationCode = activationCode;
        this.activationExpiresAt = activationExpiresAt;
        this.createdAt = createdAt;
    }



    public Long getId() {return id;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public UserStatus getStatus() {return status;}
    public String getActivationCode() {return activationCode;}
    public LocalDateTime getActivationExpiresAt() {return activationExpiresAt;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}




