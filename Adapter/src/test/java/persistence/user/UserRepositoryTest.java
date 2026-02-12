package persistence.user;

import com.cielo.adapter.persistence.user.UserEntity;
import com.cielo.adapter.persistence.user.UserJPARepository;
import com.cielo.adapter.persistence.user.UserRepository;
import enums.UserStatus;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {

    @Mock
    private UserJPARepository jpa;

    @InjectMocks
    private UserRepository repository;

    @Test
    public void existsByEmailTrueTest() {
        when(jpa.existsByEmail("test@mail.com")).thenReturn(true);

        boolean result = repository.existsByEmail("test@mail.com");

        Assertions.assertTrue(result);
    }

    @Test
    public void existsByEmailFalseTest() {
        when(jpa.existsByEmail("test@mail.com")).thenReturn(false);

        boolean result = repository.existsByEmail("test@mail.com");

        Assertions.assertFalse(result);
    }

    @Test
    public void saveUserSuccessfullyTest() {
        User user = org.mockito.Mockito.mock(User.class);

        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(10L);

        when(jpa.save(any(UserEntity.class))).thenReturn(savedEntity);

        boolean result = repository.save(user);

        Assertions.assertTrue(result);
    }

    @Test
    public void saveUserFailWhenIdNullTest() {
        User user = org.mockito.Mockito.mock(User.class);

        when(jpa.save(any(UserEntity.class)))
                .thenReturn(new UserEntity()); // id null

        boolean result = repository.save(user);

        Assertions.assertFalse(result);
    }

    @Test
    public void findAllPendingTest() {
        UserEntity entity = new UserEntity();
        entity.setId(1L);

        when(jpa.findByStatus(UserStatus.PENDING))
                .thenReturn(List.of(entity));

        List<User> result = repository.findAllPending();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void findByEmailFoundTest() {
        UserEntity entity = new UserEntity();
        entity.setId(5L);

        when(jpa.findByEmail("test@mail.com")).thenReturn(entity);

        User result = repository.findByEmail("test@mail.com");

        Assertions.assertNotNull(result);
    }

    @Test
    public void findByEmailNotFoundTest() {
        when(jpa.findByEmail("test@mail.com")).thenReturn(null);

        User result = repository.findByEmail("test@mail.com");

        Assertions.assertNull(result);
    }
}
