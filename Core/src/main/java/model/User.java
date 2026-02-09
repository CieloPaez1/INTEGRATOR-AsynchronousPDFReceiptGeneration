package model;

import enums.UserStatus;
import exception.UserException;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private final String email;
    private final String password;
    private UserStatus status;
    private String activationCode;
    private LocalDateTime activationExpiresAt;
    private final LocalDateTime createdAt;


    private User(Long id, String email, String password, UserStatus status, String activationCode,
                 LocalDateTime activationExpiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.status = status;
        this.activationCode = activationCode;
        this.activationExpiresAt = activationExpiresAt;
        this.createdAt = createdAt;
    }


    public static User factory(String email, String password, LocalDateTime now
    ) {
        if (email == null ||  email.isBlank()) {
            throw new UserException("Email cannot be null or empty");
        }
        if (!email.contains("@")) {
            throw new UserException("Invalid email format");
        }
        if (password == null || password.isBlank()) {
            throw new UserException("Password cannot be null or empty");
        }
        if (password.length() < 6) {
            throw new UserException("Password must have at least 6 characters");
        }
        if (now == null) {
            throw new UserException("Current time cannot be null");
        }

        return new User(
                null,
                email,
                password,
                UserStatus.PENDING,
                java.util.UUID.randomUUID().toString(),
                now.plusHours(24),
                now
        );
    }
    public static User restore(Long id, String email, String password, UserStatus status, String activationCode, LocalDateTime activationExpiresAt, LocalDateTime createdAt) {

        return new User(id,email, password, status, activationCode, activationExpiresAt, createdAt
        );
    }




    public void activate(LocalDateTime now) {
        if (status != UserStatus.PENDING) {
            throw new UserException("User is not in PENDING state");
        }
        if (activationExpiresAt.isBefore(now)) {
            status = UserStatus.EXPIRED;
            return;
        }

        status = UserStatus.ACTIVE;
    }


    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
    public UserStatus getStatus() {return status;}
    public Long getId() {return id;}
    public String getEmail() {return email;}
    public String getActivationCode() {return activationCode;}
    public String getPassword() {return password;}
    public LocalDateTime getActivationExpiresAt() {return activationExpiresAt;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setId(Long id) {this.id = id;}
    public void setActivationCode(String activationCode) {this.activationCode = activationCode;}
}
