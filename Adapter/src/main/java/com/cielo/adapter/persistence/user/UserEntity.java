package com.cielo.adapter.persistence.user;

import com.cielo.adapter.persistence.order.OrderEntity;
import enums.UserStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderEntity> orders = new ArrayList<>();

    public UserEntity(Long id, String email, String password, UserStatus status, String activationCode, LocalDateTime activationExpiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.status = status;
        this.activationCode = activationCode;
        this.activationExpiresAt = activationExpiresAt;
        this.createdAt = createdAt;
    }
    public UserEntity(){}



    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public UserStatus getStatus() {return status;}
    public void setStatus(UserStatus status) {this.status = status;}
    public String getActivationCode() {return activationCode;}
    public void setActivationCode(String activationCode) {this.activationCode = activationCode;}
    public LocalDateTime getActivationExpiresAt() {return activationExpiresAt;}
    public void setActivationExpiresAt(LocalDateTime activationExpiresAt) {this.activationExpiresAt = activationExpiresAt;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public List<OrderEntity> getOrders() {return orders;}
    public void setOrders(List<OrderEntity> orders) {this.orders = orders;}

    public void addOrder(OrderEntity order) {
        orders.add(order);order.setUser(this);
    }
    public void removeOrder(OrderEntity order) {
        orders.remove(order);
        order.setUser(null);
    }
}





