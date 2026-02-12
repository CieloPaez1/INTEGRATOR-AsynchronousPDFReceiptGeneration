package usecase;

import enums.UserStatus;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import output.UserOutput;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivateUserTest {

    @Mock
    private UserOutput userOutput;

    private Clock fixedClock() {
        return Clock.fixed(
                Instant.parse("2026-01-01T10:00:00Z"),
                ZoneId.systemDefault()
        );
    }

    private LocalDateTime now(Clock clock) {
        return LocalDateTime.ofInstant(clock.instant(), ZoneId.systemDefault());
    }

    private User pendingValid(Clock clock) {
        return User.factory(
                "john@example.com",
                "secret123",
                now(clock).minusHours(1)
        );
    }

    private User pendingExpired(Clock clock) {
        return User.factory(
                "expired@example.com",
                "secret123",
                now(clock).minusHours(25)
        );
    }

    @Test
    void shouldActivatePendingUser() {
        Clock clock = fixedClock();
        User user = pendingValid(clock);

        when(userOutput.findAllPending()).thenReturn(List.of(user));

        new ActivateUser(userOutput, clock).activateUser();

        verify(userOutput).save(user);
        verify(userOutput).findAllPending();
        verifyNoMoreInteractions(userOutput);

        Assertions.assertEquals(UserStatus.ACTIVE, user.getStatus());
    }

    @Test
    void shouldExpirePendingUser() {
        Clock clock = fixedClock();
        User user = pendingExpired(clock);

        when(userOutput.findAllPending()).thenReturn(List.of(user));

        new ActivateUser(userOutput, clock).activateUser();

        verify(userOutput).save(user);
        Assertions.assertEquals(UserStatus.EXPIRED, user.getStatus());
    }

    @Test
    void shouldActivateMultiplePendingUsers() {
        Clock clock = fixedClock();
        User user1 = pendingValid(clock);
        User user2 = User.factory(
                "jane@example.com",
                "secret456",
                now(clock).minusHours(2)
        );

        when(userOutput.findAllPending()).thenReturn(List.of(user1, user2));

        new ActivateUser(userOutput, clock).activateUser();

        verify(userOutput, times(2)).save(any(User.class));
        Assertions.assertEquals(UserStatus.ACTIVE, user1.getStatus());
        Assertions.assertEquals(UserStatus.ACTIVE, user2.getStatus());
    }

    @Test
    void shouldDoNothingIfNoPendingUsers() {
        Clock clock = fixedClock();

        when(userOutput.findAllPending()).thenReturn(List.of());

        new ActivateUser(userOutput, clock).activateUser();

        verify(userOutput).findAllPending();
        verify(userOutput, never()).save(any());
    }

    @Test
    void shouldStopExecutionIfActivationThrowsException() {
        Clock clock = fixedClock();
        User user = pendingValid(clock);

        // Simulamos estado inválido
        user.activate(now(clock)); // ahora ya está ACTIVE

        when(userOutput.findAllPending()).thenReturn(List.of(user));

        Assertions.assertThrows(Exception.class, () ->
                new ActivateUser(userOutput, clock).activateUser()
        );

        verify(userOutput, never()).save(any());
    }
}