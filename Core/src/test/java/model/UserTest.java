package model;

import enums.UserStatus;
import exception.UserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserTest {
    private User createPendingUser(LocalDateTime now) {
        User user= User.factory("john@example.com", "secret123", now);
        user.setId(1L);
        user.setActivationCode(UUID.randomUUID().toString());

        return user;
    }
    @Test
    void createUserInPendingState() {
        LocalDateTime now = LocalDateTime.now();

        User user = createPendingUser(now);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(UserStatus.PENDING, user.getStatus());
    }

    @Test
    void throwExceptionWhenEmailIsInvalid() {
        LocalDateTime now = LocalDateTime.now();

        Assertions.assertThrows(UserException.class, () ->
                User.factory("invalidEmail", "secret123", now)
        );
    }

    @Test
    void throwExceptionWhenPasswordIsTooShort() {
        LocalDateTime now = LocalDateTime.now();

        Assertions.assertThrows(UserException.class, () ->
                User.factory("john@example.com", "123", now)
        );
    }

    @Test
    void activateUserSuccessfully() {
        LocalDateTime now = LocalDateTime.now();
        User user = createPendingUser(now);

        user.activate(now.plusMinutes(1));

        Assertions.assertEquals(UserStatus.ACTIVE, user.getStatus());
        Assertions.assertTrue(user.isActive());
    }

    @Test
    void expireUserWhenActivationIsExpired() {
        LocalDateTime now = LocalDateTime.now();
        User user = createPendingUser(now);

        user.activate(now.plusHours(25));

        Assertions.assertEquals(UserStatus.EXPIRED, user.getStatus());
        Assertions.assertFalse(user.isActive());
    }

    @Test
    void throwExceptionWhenActivatingFromInvalidState() {
        LocalDateTime now = LocalDateTime.now();
        User user = createPendingUser(now);

        user.activate(now.plusMinutes(1));

        Assertions.assertThrows(UserException.class, () ->
                user.activate(now.plusMinutes(2))
        );
    }

    @Test
    void throwExceptionWhenActivateReceivesNullDate() {
        LocalDateTime now = LocalDateTime.now();
        User user = createPendingUser(now);

        Assertions.assertThrows(UserException.class, () ->
                user.activate(null)
        );
    }

    @Test
    void throwExceptionWhenFactoryReceivesInvalidData() {
        LocalDateTime now = LocalDateTime.now();

        Assertions.assertThrows(UserException.class, () ->
                User.factory(null, "secret123", now)
        );

        Assertions.assertThrows(UserException.class, () ->
                User.factory("", "secret123", now)
        );

        Assertions.assertThrows(UserException.class, () ->
                User.factory("john@example.com", null, now)
        );

        Assertions.assertThrows(UserException.class, () ->
                User.factory("john@example.com", "", now)
        );

        Assertions.assertThrows(UserException.class, () ->
                User.factory("john@example.com", "123", now)
        );

        Assertions.assertThrows(UserException.class, () ->
                User.factory("john@example.com", "secret123", null)
        );
    }

    @Test
    void restoreRecreatesUserWithGivenValues() {
        LocalDateTime now = LocalDateTime.now();

        User user = User.restore(
                10L,
                "john@example.com",
                "secret123",
                UserStatus.ACTIVE,
                "ABC123",
                now.plusHours(5),
                now
        );

        Assertions.assertEquals(10L, user.getId());
        Assertions.assertEquals("john@example.com", user.getEmail());
        Assertions.assertEquals("secret123", user.getPassword());
        Assertions.assertEquals(UserStatus.ACTIVE, user.getStatus());
        Assertions.assertEquals("ABC123", user.getActivationCode());
        Assertions.assertEquals(now.plusHours(5), user.getActivationExpiresAt());
        Assertions.assertEquals(now, user.getCreatedAt());
    }
}


