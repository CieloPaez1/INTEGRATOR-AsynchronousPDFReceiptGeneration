package com.cielo.adapter.persistence.user;

import model.User;


public class UserMapper {
    public static UserEntity coreToEntity(User user) {
        if (user == null) return null;

        return new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getStatus(),
                user.getActivationCode(),
                user.getActivationExpiresAt(),
                user.getCreatedAt()
        );
    }

    public static User entityToCore(UserEntity entity) {
        if (entity == null) return null;

        return User.restore(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getStatus(),
                entity.getActivationCode(),
                entity.getActivationExpiresAt(),
                entity.getCreatedAt()
        );
    }

}
