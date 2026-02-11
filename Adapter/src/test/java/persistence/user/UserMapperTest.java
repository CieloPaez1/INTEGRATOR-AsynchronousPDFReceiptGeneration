package persistence.user;

import com.cielo.adapter.persistence.user.UserEntity;
import com.cielo.adapter.persistence.user.UserMapper;
import enums.UserStatus;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class UserMapperTest {
    @Test
    void coreToEntityMapsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.restore(
                1L,
                "john@example.com",
                "secret123",
                UserStatus.PENDING,
                "code123",
                now.plusHours(24),
                now
        );

        UserEntity entity = UserMapper.coreToEntity(user);


        Assertions.assertEquals(1L, entity.getId());
        Assertions.assertEquals("john@example.com", entity.getEmail());
        Assertions.assertEquals("secret123", entity.getPassword());
        Assertions.assertEquals(UserStatus.PENDING, entity.getStatus());
        Assertions.assertEquals("code123", entity.getActivationCode());
        Assertions.assertEquals(now.plusHours(24), entity.getActivationExpiresAt());
        Assertions.assertEquals(now, entity.getCreatedAt());
    }
    @Test
    void entityToCoreMapsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        UserEntity entity = new UserEntity(
                5L,
                "john@example.com",
                "secret123",
                UserStatus.ACTIVE,
                null,
                null,
                now
        );

        User user = UserMapper.entityToCore(entity);

        Assertions.assertEquals(5L, user.getId());
        Assertions.assertEquals("john@example.com", user.getEmail());
        Assertions.assertEquals("secret123", user.getPassword());
        Assertions.assertEquals(UserStatus.ACTIVE, user.getStatus());
        Assertions.assertEquals(now, user.getCreatedAt());
    }


}


