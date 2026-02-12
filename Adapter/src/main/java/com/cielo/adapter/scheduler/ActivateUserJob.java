package com.cielo.adapter.scheduler;
import input.ActivateUserInput;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ActivateUserJob {
    private final ActivateUserInput activateUserInput;

    public ActivateUserJob(ActivateUserInput activateUserInput) {
        this.activateUserInput = activateUserInput;
    }

    @Scheduled(fixedRate = 60000)
    public void run() {
        activateUserInput.activateUser();
    }
}
