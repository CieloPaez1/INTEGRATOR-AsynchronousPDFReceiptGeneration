package com.cielo.adapter.persistence.user;

import enums.UserStatus;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import output.UserOutput;

import java.util.List;

@Repository
public class UserRepository implements UserOutput {

    private final UserJPARepository jpa;

    @Autowired
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
        return saved != null && saved.getId() != null;
    }

    @Override
    public List<User> findAllPending() {
        return jpa.findByStatus(UserStatus.PENDING)
                .stream()
                .map(UserMapper::entityToCore)
                .toList();
    }
    @Override
    public User findByEmail(String email) {
        UserEntity entity = jpa.findByEmail(email);
        return entity != null ? UserMapper.entityToCore(entity) : null;
    }
}
