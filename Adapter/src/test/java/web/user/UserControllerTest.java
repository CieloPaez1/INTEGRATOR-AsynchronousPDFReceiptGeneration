package web.user;

import com.cielo.adapter.web.user.UserController;
import com.cielo.adapter.web.user.UserDTO;
import exception.UserException;
import input.ActivateUserInput;
import input.RegisterUserInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private RegisterUserInput registerUser;

    @Mock
    private ActivateUserInput activateUser;

    @InjectMocks
    private UserController userController;

    @Test
    void createUserSuccessfully() {

        UserDTO dto = new UserDTO();
        dto.setEmail("john@example.com");
        dto.setPassword("secret123");

        ResponseEntity<Void> expected =
                ResponseEntity.status(HttpStatus.CREATED).build();

        ResponseEntity<Void> result =
                userController.createUser(dto);

        Assertions.assertEquals(expected.getStatusCode(), result.getStatusCode());

        verify(registerUser).registerUser("john@example.com", "secret123");
    }

    @Test
    void createUserThrowsException() {

        UserDTO dto = new UserDTO();
        dto.setEmail("john@example.com");
        dto.setPassword("secret123");

        doThrow(new UserException("Email already exists"))
                .when(registerUser)
                .registerUser(any(), any());

        Assertions.assertThrows(UserException.class, () ->
                userController.createUser(dto)
        );

        verify(registerUser).registerUser("john@example.com", "secret123");
    }

    @Test
    void activateUserSuccessfully() {

        ResponseEntity<Void> expected =
                ResponseEntity.status(HttpStatus.OK).build();

        ResponseEntity<Void> result =
                userController.activateUser();

        Assertions.assertEquals(expected.getStatusCode(), result.getStatusCode());

        verify(activateUser).activateUser();
    }

    @Test
    void activateUserThrowsException() {

        doThrow(new UserException("Activation failed"))
                .when(activateUser)
                .activateUser();

        Assertions.assertThrows(UserException.class, () ->
                userController.activateUser()
        );

        verify(activateUser).activateUser();
    }
}
