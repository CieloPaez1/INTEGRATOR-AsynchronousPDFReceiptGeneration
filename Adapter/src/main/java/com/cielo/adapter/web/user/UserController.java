package com.cielo.adapter.web.user;


import input.ActivateUserInput;
import input.RegisterUserInput;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/users")
public class UserController {

    private final RegisterUserInput registerUser;
    private final ActivateUserInput activateUser;
    public UserController(RegisterUserInput registerUser, ActivateUserInput activateUser) {
        this.registerUser = registerUser;
        this.activateUser = activateUser;
    }


    @PostMapping
    public ResponseEntity<Void> createUser(
            @Valid @RequestBody UserDTO request) {

        registerUser.registerUser(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/activate")
    public ResponseEntity<Void> activateUser() {
        activateUser.activateUser();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}