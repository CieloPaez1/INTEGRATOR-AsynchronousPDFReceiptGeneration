package com.cielo.adapter.persistence.user;

import enums.UserStatus;
import model.User;
import org.springframework.stereotype.Repository;
import output.UserOutput;

import java.util.List;

@Repository
public class UserRepository implements UserOutput {

    private final UserJPARepository jpa;

    public UserRepository(UserJPARepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public boolean save(User user) {
        UserEntity entity = UserMapper.coreToEntity(user);
        UserEntity saved = jpa.save(entity);
        return saved.getId() != null;
    }

    @Override
    public List<User> findAllPending() {
        return jpa.findByStatus(UserStatus.PENDING)
                .stream()
                .map(UserMapper::entityToCore)
                .toList();
    }
}
