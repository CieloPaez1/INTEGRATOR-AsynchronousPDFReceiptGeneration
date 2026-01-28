package usecase;

import input.ActivateUserInput;
import model.User;
import output.UserOutput;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class ActivateUser implements ActivateUserInput {
    private final UserOutput userOutput;
    private final Clock clock;

    public ActivateUser(UserOutput userOutput, Clock clock) {
        this.userOutput = userOutput;
        this.clock = clock;
    }

    @Override
    public void activateUser() {

        List<User> pending = userOutput.findAllPending();
        LocalDateTime now = LocalDateTime.now(clock);

        for (User user : pending) {
            user.activate(now);
            userOutput.save(user);
        }
    }


}
