package ua.mate.team3.carsharingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.mate.team3.carsharingapp.dto.user.auth.UserLoginRequestDto;
import ua.mate.team3.carsharingapp.dto.user.auth.UserLoginResponseDto;
import ua.mate.team3.carsharingapp.dto.user.auth.UserRegistrationRequestDto;
import ua.mate.team3.carsharingapp.dto.user.auth.UserRegistrationResponseDto;
import ua.mate.team3.carsharingapp.exception.RegistrationException;
import ua.mate.team3.carsharingapp.service.AuthenticationService;
import ua.mate.team3.carsharingapp.service.UserService;

@Tag(name = "Auth controller", description = "Endpoints for managing authorization")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Login of user", description = "Validate user`s login and password,"
            + " if all input parameters is valid, return created JWT Token to current user.")
    @PostMapping("/register")
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto registrationRequest)
            throws RegistrationException {
        return userService.register(registrationRequest);
    }

    @Operation(summary = "Registration of user", description = "Checks user input parameters"
            + " and validate them, if email does not exist in data base,"
            + " and password and repeat password are equals, saves the user to db.")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto loginRequestDto) {
        return authenticationService.authenticate(loginRequestDto);
    }
}
