package ua.mate.team3.carsharingapp.service;

import ua.mate.team3.carsharingapp.dto.user.auth.UserLoginRequestDto;
import ua.mate.team3.carsharingapp.dto.user.auth.UserLoginResponseDto;
import ua.mate.team3.carsharingapp.model.User;

public interface AuthenticationService {

    UserLoginResponseDto authenticate(UserLoginRequestDto requestDto);

    Long getUserId();

    User getUser();
}
