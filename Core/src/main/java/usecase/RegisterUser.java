package usecase;

import exception.UserException;
import input.RegisterUserInput;
import model.User;
import output.UserOutput;

import java.time.Clock;
import java.time.LocalDateTime;

public class RegisterUser implements RegisterUserInput {

    private final UserOutput userOutput;
    private final Clock clock;

    public RegisterUser(UserOutput userOutput, Clock clock) {
        this.userOutput = userOutput;
        this.clock = clock;
    }

    @Override
    public void registerUser(String email, String password) {

        if (userOutput.existsByEmail(email)) {
            throw new UserException("Email already exists");
        }

        LocalDateTime now = LocalDateTime.now(clock);
        User user = User.factory(email, password, now);

        if (!userOutput.save(user)) {
            throw new UserException("Could not save user");
        }
    }
}


