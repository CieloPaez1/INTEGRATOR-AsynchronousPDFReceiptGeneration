package output;

import model.User;

import java.util.List;

public interface UserOutput {
    boolean existsByEmail(String email);
    boolean save(User user);
    List<User> findAllPending();
    User findByEmail(String email);
}

