package usecase;

import exception.UserException;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import output.UserOutput;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserTest {
    @Mock
    private UserOutput userOutput;

    private Clock fixedClock() {
        return Clock.fixed(
                Instant.parse("2026-02-01T10:00:00Z"),
                ZoneId.systemDefault()
        );
    }

    private RegisterUser useCase(Clock clock) {
        return new RegisterUser(userOutput, clock);
    }


    @Test
    void shouldRegisterUserSuccessfully() {
        Clock clock = fixedClock();

        when(userOutput.existsByEmail("john@example.com")).thenReturn(false);
        when(userOutput.save(any(User.class))).thenReturn(true);

        useCase(clock).registerUser("john@example.com", "secret123");

        verify(userOutput).existsByEmail("john@example.com");
        verify(userOutput).save(any(User.class));
    }


    @Test
    void shouldFailWhenEmailAlreadyExists() {
        Clock clock = fixedClock();

        when(userOutput.existsByEmail("john@example.com")).thenReturn(true);

        Assertions.assertThrows(UserException.class, () ->
                useCase(clock).registerUser("john@example.com", "secret123")
        );

        verify(userOutput).existsByEmail("john@example.com");
        verify(userOutput, never()).save(any());
    }

    @Test
    void shouldFailWhenSaveReturnsFalse() {
        Clock clock = fixedClock();

        when(userOutput.existsByEmail("john@example.com")).thenReturn(false);
        when(userOutput.save(any(User.class))).thenReturn(false);

        Assertions.assertThrows(UserException.class, () ->
                useCase(clock).registerUser("john@example.com", "secret123")
        );

        verify(userOutput).save(any(User.class));
    }

    @Test
    void shouldFailWhenEmailIsInvalid() {
        Clock clock = fixedClock();

        when(userOutput.existsByEmail("badEmail")).thenReturn(false);

        Assertions.assertThrows(UserException.class, () ->
                useCase(clock).registerUser("badEmail", "secret123")
        );

        verify(userOutput, never()).save(any());
    }

    @Test
    void shouldFailWhenPasswordIsInvalid() {
        Clock clock = fixedClock();

        when(userOutput.existsByEmail("john@example.com")).thenReturn(false);

        Assertions.assertThrows(UserException.class, () ->
                useCase(clock).registerUser("john@example.com", "123")
        );

        verify(userOutput, never()).save(any());
    }

    @Test
    void shouldFailWhenPasswordIsEmpty() {
        Clock clock = fixedClock();

        when(userOutput.existsByEmail("john@example.com")).thenReturn(false);

        Assertions.assertThrows(UserException.class, () ->
                useCase(clock).registerUser("john@example.com", "")
        );

        verify(userOutput, never()).save(any());
    }
}



